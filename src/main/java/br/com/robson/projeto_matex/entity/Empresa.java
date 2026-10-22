package br.com.robson.projeto_matex.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Empresa {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
	private List<Usuario> usuarios;

	@NotBlank
	@Column(name = "nome_fantasia")
	private String nomeFantasia;
	
	@Column(name = "razao_social")
	private String razaoSocial;
	
	@NotBlank
	@Column(unique= true)
	@Pattern(regexp = "^[0-9A-Z]{12}[0-9]{2}$", message = "CNPJ fora do padrão.")
	private String cnpj;
	
	@Size(max = 11, message = "Telefone inválido.")
	private String telefone;
	
	@NotNull
	private Boolean ativa;
	
	@Valid
	@Embedded
	@NotNull
	private Endereco endereco;
	
	@Valid
	@Embedded
	@NotNull
	private Auditoria auditoria = new Auditoria();
	
	
	
	@PrePersist
	void aoCriar() {
		auditoria.registrarCriacao();
	}
	
	@PreUpdate
	void aoAtualizar() {
		auditoria.registrarAtualizacao();
	}

	public Long getId() {
		return id;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public Boolean getAtiva() {
		return ativa;
	}

	public void setAtiva(Boolean ativa) {
		this.ativa = ativa;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public Auditoria getAuditoria() {
		return auditoria;
	}
	

}
