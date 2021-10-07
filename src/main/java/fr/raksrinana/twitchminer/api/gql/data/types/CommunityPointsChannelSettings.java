package fr.raksrinana.twitchminer.api.gql.data.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.util.List;

@JsonTypeName("CommunityPointsChannelSettings")
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"}, callSuper = true)
public class CommunityPointsChannelSettings extends GQLType{
	@JsonProperty("name")
	private String name;
	@JsonProperty("image")
	private CommunityPointsImage image;
	@JsonProperty("automaticRewards")
	private List<CommunityPointsAutomaticReward> automaticRewards;
	@JsonProperty("customRewards")
	private List<CommunityPointsCustomReward> customRewards;
	@JsonProperty("goals")
	private List<CommunityPointsCommunityGoal> goals;
	@JsonProperty("isEnabled")
	private boolean enabled;
	@JsonProperty("raidPointAmount")
	private long raidPointAmount;
	@JsonProperty("emoteVariants")
	private List<CommunityPointsEmoteVariant> emoteVariants;
	@JsonProperty("earning")
	private CommunityPointsChannelEarningSettings earning;
	
	public CommunityPointsChannelSettings(){
		super("CommunityPointsChannelSettings");
	}
}