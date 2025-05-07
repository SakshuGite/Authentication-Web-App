package in.bushansirgur.authify.io;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileRequest {

    @NotBlank(message = "Name should not be Empty")
    private String name;

    @Email(message = "Enter valid email address")
    @NotNull(message = "Email should not be empty")
    private String email;

    @Size(min = 6, message = "Password should be at least 6 characters")
    private String password;

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
