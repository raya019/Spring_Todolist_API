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
public class RegisterRequest {
    @NotBlank(message = "Nama tidak boleh kosong")
    @Size(max = 30, message = "panjang Nama maximal 30 character")
    private String name;

    @NotBlank(message = "Email tidak boleh kosong")
    @Size(max = 30, message = "panjang Email maximal 30 character")
//    @Pattern(regexp = "^[\\\\w!#$%&’*+/=?`{|}~^-]+(?:\\\\.[\\\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,6}$", message = "Email tidak valid")
    private String email;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 8, message = "panjang Password minimal 8 character")
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@#$%^&+=]).{8,}$", message = "Password tidak valid")
    private String password;

    @NotBlank(message = "Phone tidak boleh kosong")
    @Size(max = 15)
    private String phone;
}
