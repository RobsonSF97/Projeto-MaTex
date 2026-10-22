package br.com.robson.projeto_matex.entity;

import br.com.robson.projeto_matex.enums.Uf;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Embeddable
public class Endereco {
	
	@NotBlank
	@Pattern(regexp = "^[0-9]+$", message="Formato de CEP inválido.")
	@Size(min = 8, max = 8)
	private String cep;
	
	@NotBlank
	@Size(max = 100, message = "Quantidade de characteres maior que o estipulado.")
	private String rua;
	
	@NotBlank
	@Size(max = 30, message = "Quantidade de characteres maior que o estipulado.")
	private String numero;
	
	@NotBlank
	@Size(max = 100, message = "Quantidade de characteres maior que o estipulado.")
	private String bairro;
	
	@NotBlank
	@Size(max = 100, message = "Quantidade de characteres maior que o estipulado.")
	private String cidade;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private Uf uf;
	
	@Size(max = 100, message = "Quantidade de characteres maior que o estipulado.")
	private String complemento;

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getRua() {
		return rua;
	}

	public void setRua(String rua) {
		this.rua = rua;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public Uf getUf() {
		return uf;
	}

	public void setUf(Uf uf) {
		this.uf = uf;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	
	

}
