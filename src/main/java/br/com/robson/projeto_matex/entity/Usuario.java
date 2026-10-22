package br.com.robson.projeto_matex.entity;

import java.util.Locale;

import br.com.robson.projeto_matex.enums.Perfil;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "empresa_id", nullable = false)
	private Empresa empresa;
	
	@NotBlank
	@Size(max = 100, message = "Quantidade de characteres maior que o estipulado.")
	private String nome;
	
	@NotBlank
	@Column(unique=true)
	@Email(message = "E-mail inválido")
	@Size(max = 100, message = "Quantidade de characteres maior que o estipulado.")
	private String email;
	
	@NotBlank
	@Column(name = "senha_hash")
	@Size(max = 255, message = "Quantidade de characteres maior que o estipulado.")
	private String senhaHash;
	
	
	@Enumerated(EnumType.STRING)
	@NotNull
	private Perfil perfil;
	
	@NotNull
	private Boolean ativo;
	
	@Valid
	@Embedded
	@NotNull
	private Auditoria auditoria = new Auditoria();
	
	protected Usuario() {
		
	}
	
	public Usuario(Empresa empresa, String nome, String email, String senhaHash, Perfil perfil) {
		this.empresa = empresa;
		this.nome = nome;
		this.email = normalizarEmail(email);
		this.senhaHash = senhaHash;
		this.perfil = perfil;
		this.ativo = true;
	}
	
	@PrePersist
	void aoCriar() {
		auditoria.registrarCriacao();
	}
	
	@PreUpdate
	void aoAtualizar() {
		auditoria.registrarAtualizacao();
	}
	
	public static String normalizarEmail(String email) {
		return email == null ? null : email.strip().toLowerCase(Locale.ROOT);
	}

	public Long getId() {
		return id;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = normalizarEmail(email);
	}

	public String getSenhaHash() {
		return senhaHash;
	}

	public void setSenhaHash(String senhaHash) {
		this.senhaHash = senhaHash;
	}


	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Auditoria getAuditoria() {
		return auditoria;
	}

	
	
	
	
	
	

}
