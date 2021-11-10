package fr.raksrinana.channelpointsminer.streamer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.channelpointsminer.prediction.bet.action.PredictionAction;
import fr.raksrinana.channelpointsminer.prediction.bet.amount.AmountCalculator;
import fr.raksrinana.channelpointsminer.prediction.bet.amount.PercentageAmount;
import fr.raksrinana.channelpointsminer.prediction.bet.outcome.OutcomePicker;
import fr.raksrinana.channelpointsminer.prediction.bet.outcome.SmartOutcomePicker;
import fr.raksrinana.channelpointsminer.prediction.delay.DelayCalculator;
import fr.raksrinana.channelpointsminer.prediction.delay.FromEndDelay;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class PredictionSettings{
	@JsonProperty("delayCalculator")
	@NotNull
	@Builder.Default
	private DelayCalculator delayCalculator = FromEndDelay.builder().seconds(10).build();
	@JsonProperty("minimumPointsRequired")
	@Builder.Default
	private int minimumPointsRequired = 0;
	@JsonProperty("outcomePicker")
	@NotNull
	@Builder.Default
	private OutcomePicker outcomePicker = SmartOutcomePicker.builder().percentageGap(.2f).build();
	@JsonProperty("amountCalculator")
	@NotNull
	@Builder.Default
	private AmountCalculator amountCalculator = PercentageAmount.builder().percentage(.2F).max(50_000).build();
	@JsonProperty("actions")
	@NotNull
	@Builder.Default
	private List<PredictionAction> actions = new ArrayList<>();
	
	public PredictionSettings(PredictionSettings origin){
		delayCalculator = origin.delayCalculator;
		minimumPointsRequired = origin.minimumPointsRequired;
		outcomePicker = origin.outcomePicker;
		amountCalculator = origin.amountCalculator;
		actions = origin.actions;
	}
}