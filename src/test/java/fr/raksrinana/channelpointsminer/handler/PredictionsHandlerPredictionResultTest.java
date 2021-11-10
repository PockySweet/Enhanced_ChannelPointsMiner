package fr.raksrinana.channelpointsminer.handler;

import fr.raksrinana.channelpointsminer.api.ws.data.message.PredictionResult;
import fr.raksrinana.channelpointsminer.api.ws.data.message.predictionresult.PredictionResultData;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.PredictionResultPayload;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.PredictionResultType;
import fr.raksrinana.channelpointsminer.api.ws.data.request.topic.Topic;
import fr.raksrinana.channelpointsminer.handler.data.PlacedPrediction;
import fr.raksrinana.channelpointsminer.miner.IMiner;
import fr.raksrinana.channelpointsminer.prediction.bet.BetPlacer;
import fr.raksrinana.channelpointsminer.streamer.Streamer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.ZonedDateTime;
import java.util.Optional;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredictionsHandlerPredictionResultTest{
	private static final String STREAMER_ID = "streamer-id";
	private static final String EVENT_ID = "event-id";
	private static final ZonedDateTime EVENT_DATE = ZonedDateTime.of(2021, 10, 10, 11, 59, 0, 0, UTC);
	private static final int AMOUNT = 50;
	
	@InjectMocks
	private PredictionsHandler tested;
	
	@Mock
	private IMiner miner;
	@Mock
	private BetPlacer betPlacer;
	@Mock
	private PredictionResult predictionResult;
	@Mock
	private PredictionResultData predictionResultData;
	@Mock
	private fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Prediction wsPrediction;
	@Mock
	private PredictionResultPayload predictionResultPayload;
	@Mock
	private Topic topic;
	@Mock
	private Streamer streamer;
	
	@BeforeEach
	void setUp(){
		lenient().when(topic.getTarget()).thenReturn(STREAMER_ID);
		lenient().when(miner.getStreamerById(STREAMER_ID)).thenReturn(Optional.of(streamer));
		
		lenient().when(predictionResult.getData()).thenReturn(predictionResultData);
		lenient().when(predictionResultData.getPrediction()).thenReturn(wsPrediction);
		lenient().when(wsPrediction.getEventId()).thenReturn(EVENT_ID);
		lenient().when(wsPrediction.getChannelId()).thenReturn(STREAMER_ID);
		lenient().when(wsPrediction.getResult()).thenReturn(predictionResultPayload);
		lenient().when(predictionResultPayload.getType()).thenReturn(PredictionResultType.WIN);
		lenient().when(predictionResultPayload.getPointsWon()).thenReturn(100);
		
		lenient().when(streamer.getId()).thenReturn(STREAMER_ID);
	}
	
	@Test
	void noResult(){
		when(wsPrediction.getResult()).thenReturn(null);
		
		assertDoesNotThrow(() -> tested.handle(topic, predictionResult));
	}
	
	@Test
	void noPredictionPlacedData(){
		assertDoesNotThrow(() -> tested.handle(topic, predictionResult));
	}
	
	@Test
	void removedPredictionPlaced(){
		var prediction = mock(fr.raksrinana.channelpointsminer.handler.data.Prediction.class);
		var predictionPlaced = mock(PlacedPrediction.class);
		when(predictionPlaced.getAmount()).thenReturn(AMOUNT);
		
		tested.getPredictions().put(EVENT_ID, prediction);
		tested.getPlacedPredictions().put(EVENT_ID, predictionPlaced);
		
		assertDoesNotThrow(() -> tested.handle(topic, predictionResult));
		assertThat(tested.getPlacedPredictions()).isEmpty();
		assertThat(tested.getPredictions()).isEmpty();
	}
}