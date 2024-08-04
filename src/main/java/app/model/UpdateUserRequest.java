package app.model;

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
public class UpdateUserRequest {

    @Size(max = 30, message = "panjang Nama maximal 30 character")
    private String name;

    @Size(max = 30, message = "panjang Email maximal 30 character")
    //    @Pattern(regexp = "^[\\\\w!#$%&’*+/=?`{|}~^-]+(?:\\\\.[\\\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,6}$", message = "Email tidak valid")
    private String email;

    //    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@#$%^&+=]).{8,}$", message = "Password tidak valid")
    @Size(min = 8, message = "panjang Password minimal 8 character")
    private String password;

    @Size(max = 15)
    private String phone;
}
