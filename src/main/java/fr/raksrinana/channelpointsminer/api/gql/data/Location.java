package fr.raksrinana.channelpointsminer.api.gql.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Location{
	@JsonProperty("line")
	private int line;
	@JsonProperty("column")
	private int column;
}