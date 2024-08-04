package app.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoAddRequest {

    @NotBlank(message = "Todolist Tidak Boleh Kosong")
    @Size(max = 100, message = "panjang Todolist harus 255 character")
    private String todo;

    @Size(max = 255, message = "panjang Description harus 255 character")
    private String description;

}
