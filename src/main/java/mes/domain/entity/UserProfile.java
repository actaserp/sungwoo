package mes.domain.entity;

import java.io.Serializable;
import java.sql.ConnectionBuilder;
import java.sql.Timestamp;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="user_profile")
@EqualsAndHashCode(callSuper=false)
public class UserProfile extends AbstractAuditModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "\"Name\"")
	String name;
	
	@Column(name = "\"CompCode\"")
	String compCode;
	
	@Column(name = "\"lang_code\"")
	String LangCode;
	
	@Column(name = "\"Company_id\"")
	Integer CompanyId;
	
	@Column(name = "\"Depart_id\"")
	Integer DepartId;
	
	@Column(name = "\"Factory_id\"")
	Integer FactoryId;
	
	@Column(name = "password_changed")
	Timestamp PasswordChanged;
	
	@Column(name = "\"token\"")
	String Token;

	@Column(name = "spworkcd")
	String spworkcd;

	@Column(name = "spworknm")
	String spworknm;

	@Column(name = "spcompcd")
	String spcompcd;

	@Column(name = "spcompnm")
	String spcompnm;

	@Column(name = "spplancd")
	String spplancd;

	@Column(name = "spplannm")
	String spplannm;


	@JsonBackReference
	@Id
	@OneToOne
	@JoinColumn(name = "\"User_id\"")
	private User user;


	@ManyToOne
	@JoinColumn(name="\"UserGroup_id\"")
	UserGroup userGroup;
}
