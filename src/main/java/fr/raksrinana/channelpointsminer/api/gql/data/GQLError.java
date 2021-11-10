package fr.raksrinana.channelpointsminer.api.gql.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class GQLError{
	@JsonProperty("message")
	@NotNull
	private String message;
	@JsonProperty("locations")
	@NotNull
	@Builder.Default
	private List<Location> locations = new LinkedList<>();
	@JsonProperty("path")
	@NotNull
	@Builder.Default
	private List<String> path = new LinkedList<>();
}