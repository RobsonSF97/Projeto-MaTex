package br.com.robson.projeto_matex.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class Auditoria {
	
	@NotNull
	@Column(name = "criado_em", nullable = false, updatable = false)
	private Instant criadoEm;
	
	@Column(name = "atualizado_em")
	private Instant atualizadoEm;

	void registrarCriacao() {
		this.criadoEm = Instant.now();
	}

	void registrarAtualizacao() {
		this.atualizadoEm = Instant.now();
	}

	public Instant getAtualizadoEm() {
		return atualizadoEm;
	}
	
	public Instant getCriadoEm() {
		return criadoEm;
	}

	
	

}
