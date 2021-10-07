package fr.raksrinana.twitchminer.api.ws.data.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = ViewCount.class, name = "viewcount"),
		@JsonSubTypes.Type(value = GlobalLastViewedContentUpdated.class, name = "global-last-viewed-content-updated"),
		@JsonSubTypes.Type(value = ChannelLastViewedContentUpdated.class, name = "channel-last-viewed-content-updated"),
		@JsonSubTypes.Type(value = PointsEarned.class, name = "points-earned"),
		@JsonSubTypes.Type(value = ClaimAvailable.class, name = "claim-available"),
		@JsonSubTypes.Type(value = ClaimClaimed.class, name = "claim-claimed"),
		@JsonSubTypes.Type(value = StreamDown.class, name = "stream-down"),
		@JsonSubTypes.Type(value = StreamUp.class, name = "stream-up"),
		@JsonSubTypes.Type(value = EventUpdated.class, name = "event-updated"),
})
@ToString
public abstract class Message{
	@JsonProperty("type")
	private String type;
	
	public Message(String type){
		this.type = type;
	}
}