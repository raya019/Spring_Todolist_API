package app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoUpdateRequest {

    @NotBlank(message = "id tidak boleh kosong")
    @JsonIgnore
    private String id;

    @Size(max = 100, message = "todolist tidak boleh kosong")
    @NotBlank(message = "todolist tidak boleh kosong")
    private String todo;

    @NotBlank(message = "description tidak boleh kosong")
    @Size(max = 255, message = "description tidak boleh kosong")
    private String description;

    @NotNull(message = "Tidak Boleh Kosong")
    private boolean isDone;

}
