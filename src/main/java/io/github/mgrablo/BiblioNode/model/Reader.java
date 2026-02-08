package io.github.mgrablo.BiblioNode.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "readers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Reader extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false)
	private String fullName;

	@Email
	@NotBlank
	@Column(nullable = false, unique = true)
	private String email;

	@OneToMany(mappedBy = "reader")
	List<Loan> loans = new ArrayList<>();
}
