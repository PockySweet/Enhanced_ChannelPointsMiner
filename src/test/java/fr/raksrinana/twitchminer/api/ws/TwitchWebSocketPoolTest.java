package fr.raksrinana.twitchminer.api.ws;

import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.twitchminer.api.ws.data.request.topic.Topics;
import fr.raksrinana.twitchminer.api.ws.data.response.TwitchWebSocketResponse;
import fr.raksrinana.twitchminer.factory.TimeFactory;
import fr.raksrinana.twitchminer.factory.TwitchWebSocketClientFactory;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.Instant;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.java_websocket.framing.CloseFrame.ABNORMAL_CLOSE;
import static org.java_websocket.framing.CloseFrame.NORMAL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwitchWebSocketPoolTest{
	private static final Instant NOW = Instant.parse("2021-10-10T10:10:10Z");
	
	private final TwitchWebSocketPool tested = new TwitchWebSocketPool();
	
	@Mock
	private Topics topics;
	@Mock
	private Topic topic;
	@Mock
	private TwitchWebSocketClient client;
	@Mock
	private TwitchWebSocketResponse twitchWebSocketResponse;
	@Mock
	private TwitchWebSocketListener twitchWebSocketListener;
	
	@BeforeEach
	void setUp(){
		lenient().when(topics.getTopics()).thenReturn(Set.of(topic));
	}
	
	@Test
	void addTopicCreatesNewClient() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client).addListener(tested);
			verify(client).connectBlocking();
			verify(client).listenTopic(topics);
		}
	}
	
	@Test
	void addNewTopicToExistingClient() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			
			when(client.isTopicListened(topic)).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client).addListener(tested);
			verify(client).connectBlocking();
			verify(client, times(2)).listenTopic(topics);
		}
	}
	
	@Test
	void addExistingTopicToExistingClient() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			
			when(client.isTopicListened(topic)).thenReturn(true);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client).addListener(tested);
			verify(client).connectBlocking();
			verify(client).listenTopic(topics);
		}
	}
	
	@Test
	void manyTopicsAreSplitOnSeveralClients(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var client2 = mock(TwitchWebSocketClient.class);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client).thenReturn(client2);
			
			when(client.isTopicListened(any())).thenReturn(false);
			when(client.getTopicCount()).thenReturn(0);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			when(client.getTopicCount()).thenReturn(50);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			
			assertThat(tested.getClientCount()).isEqualTo(2);
		}
	}
	
	@Test
	void clientError() throws InterruptedException{
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			
			doThrow(new RuntimeException("For tests")).when(client).connectBlocking();
			
			assertThrows(RuntimeException.class, () -> tested.listenTopic(topics));
			
			assertThat(tested.getClientCount()).isEqualTo(0);
		}
	}
	
	@Test
	void closesAllClients(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var client2 = mock(TwitchWebSocketClient.class);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client).thenReturn(client2);
			
			when(client.isTopicListened(any())).thenReturn(false);
			when(client.getTopicCount()).thenReturn(0);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			when(client.getTopicCount()).thenReturn(50);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			
			assertDoesNotThrow(tested::close);
			
			verify(client).close();
			verify(client2).close();
		}
	}
	
	@Test
	void normalClientCloseRemovesClient(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(() -> tested.onWebSocketClosed(client, NORMAL, "test", false));
			assertThat(tested.getClientCount()).isEqualTo(0);
		}
	}
	
	@Test
	void abnormalClientCloseRecreatesClient(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class)){
			var client2 = mock(TwitchWebSocketClient.class);
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client).thenReturn(client2);
			
			var topics = Topics.builder().topics(Set.of(topic)).build();
			
			when(client.getTopics()).thenReturn(Set.of(topics));
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(() -> tested.onWebSocketClosed(client, ABNORMAL_CLOSE, "test", true));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			verify(client2).listenTopic(topics);
		}
	}
	
	@Test
	void messagesAreRedirected(){
		assertDoesNotThrow(() -> tested.addListener(twitchWebSocketListener));
		assertDoesNotThrow(() -> tested.onWebSocketMessage(twitchWebSocketResponse));
		
		verify(twitchWebSocketListener).onWebSocketMessage(twitchWebSocketResponse);
	}
	
	@Test
	void pingSendsPing(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(client.getLastPong()).thenReturn(NOW.minusSeconds(10).toEpochMilli());
			when(client.isOpen()).thenReturn(true);
			when(client.isClosing()).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(tested::ping);
		}
	}
	
	@Test
	void pingTimedOutClosing(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(client.getLastPong()).thenReturn(NOW.minusSeconds(600).toEpochMilli());
			when(client.isOpen()).thenReturn(true);
			when(client.isClosing()).thenReturn(true);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(tested::ping);
			
			verify(client).close(eq(ABNORMAL_CLOSE), anyString());
		}
	}
	
	@Test
	void pingTimedOutClosed(){
		try(var twitchClientFactory = Mockito.mockStatic(TwitchWebSocketClientFactory.class);
				var timeFactory = Mockito.mockStatic(TimeFactory.class)){
			twitchClientFactory.when(TwitchWebSocketClientFactory::createClient).thenReturn(client);
			timeFactory.when(TimeFactory::now).thenReturn(NOW);
			
			when(client.getLastPong()).thenReturn(NOW.minusSeconds(600).toEpochMilli());
			when(client.isOpen()).thenReturn(false);
			
			assertDoesNotThrow(() -> tested.listenTopic(topics));
			assertThat(tested.getClientCount()).isEqualTo(1);
			
			assertDoesNotThrow(tested::ping);
			
			verify(client).close(eq(ABNORMAL_CLOSE), anyString());
		}
	}
}