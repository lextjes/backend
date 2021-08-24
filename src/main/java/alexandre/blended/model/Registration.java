package alexandre.blended.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "registrations")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Registration {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @Column
    @NotBlank
    private String activityId;

    @Column
    @NotBlank
    private String firstName;

    @NotBlank
    @Column
    private String lastName;

    @Column
    @NotBlank
    private String telephoneNumber;

    @Email
    @Column
    private String email;

    @Column
    @NotBlank
    private String date;

    @Column
    private String cancelledDate;
}
