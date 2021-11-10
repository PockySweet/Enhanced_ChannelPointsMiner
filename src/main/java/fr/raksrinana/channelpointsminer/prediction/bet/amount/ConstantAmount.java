package fr.raksrinana.channelpointsminer.prediction.bet.amount;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.channelpointsminer.api.ws.data.message.subtype.Outcome;
import fr.raksrinana.channelpointsminer.handler.data.Prediction;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@JsonTypeName("mostUsers")
@Getter
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
public class ConstantAmount implements AmountCalculator{
	private int amount;
	
	@Override
	public int calculateAmount(@NotNull Prediction prediction, @NotNull Outcome outcome){
		return amount;
	}
}