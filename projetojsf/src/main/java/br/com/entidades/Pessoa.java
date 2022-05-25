package br.com.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
public class Pessoa implements Serializable{
	

	private static final long serialVersionUID = 1L;

		@Id
		@GeneratedValue (strategy = GenerationType.AUTO)
		private Long id;
		
		private String nome;
		
		private String sobrenome;
		
		private Integer idade;
		
		private String sexo;
		
		private String[] frameworks;
		
		private Boolean ativo;  //colocar o Objeto Boolean (maiúsculo)
		
		private String login;
		
		private String nivelProgramador;
		
		private String senha;
		
		private String perfilUser;
		
		private Integer[] linguagens;
		
		private String cep;
		
		private String logradouro;
		
		private String complemento;
		
		private String bairro;
		
		private String localidade;
		
		private String uf;
				
		private String ibge;
		
		private String gia;
		
		
		
	

		@Temporal(TemporalType.DATE)
		private Date dataNascimento = new Date();
		
		
		public Pessoa() {
		}
		
		
		
		
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getNome() {
			return nome;
		}
		public void setNome(String nome) {
			this.nome = nome;
		}
		public String getSobrenome() {
			return sobrenome;
		}
		public void setSobrenome(String sobrenome) {
			this.sobrenome = sobrenome;
		}
		public Integer getIdade() {
			return idade;
		}
		public void setIdade(Integer idade) {
			this.idade = idade;
		}
		public Date getDataNascimento() {
			return dataNascimento;
		}
		public void setDataNascimento(Date dataNascimento) {
			this.dataNascimento = dataNascimento;
		}
		
		public String getSexo() {
			return sexo;
		}

		public void setSexo(String sexo) {
			this.sexo = sexo;
		}

		
		
		public String[] getFrameworks() {
			return frameworks;
		}



		public void setFrameworks(String[] frameworks) {
			this.frameworks = frameworks;
		}

		
		public Boolean getAtivo() {
			return ativo;
		}


		public void setAtivo(Boolean ativo) {
			this.ativo = ativo;
		}
		
		public String getLogin() {
			return login;
		}




		public void setLogin(String login) {
			this.login = login;
		}




		public String getSenha() {
			return senha;
		}




		public void setSenha(String senha) {
			this.senha = senha;
		}


		public String getPerfilUser() {
			return perfilUser;
		}




		public void setPerfilUser(String perfilUser) {
			this.perfilUser = perfilUser;
		}


		public String getNivelProgramador() {
			return nivelProgramador;
		}




		public void setNivelProgramador(String nivelProgramador) {
			this.nivelProgramador = nivelProgramador;
		}

		
		public void setLinguagens(Integer[] linguagens) {
			this.linguagens = linguagens;
		}
		
		public Integer[] getLinguagens() {
			return linguagens;
		}
		
		public void setCep(String cep) {
			this.cep = cep;
		}
		public String getCep() {
			return cep;
		}
	


		public String getLogradouro() {
			return logradouro;
		}




		public void setLogradouro(String logradouro) {
			this.logradouro = logradouro;
		}




		public String getComplemento() {
			return complemento;
		}




		public void setComplemento(String complemento) {
			this.complemento = complemento;
		}




		public String getBairro() {
			return bairro;
		}




		public void setBairro(String bairro) {
			this.bairro = bairro;
		}




		public String getLocalidade() {
			return localidade;
		}




		public void setLocalidade(String localidade) {
			this.localidade = localidade;
		}




		public String getUf() {
			return uf;
		}




		public void setUf(String uf) {
			this.uf = uf;
		}


		public String getIbge() {
			return ibge;
		}




		public void setIbge(String ibge) {
			this.ibge = ibge;
		}




		public String getGia() {
			return gia;
		}




		public void setGia(String gia) {
			this.gia = gia;
		}
		

		
		
		/* No banco de dados vai usar o Id para separar os registros. Equal e HashCode é o atributo q vai diferenciar*/






		@Override
		public int hashCode() {
			return Objects.hash(id);
		}



		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pessoa other = (Pessoa) obj;
			return Objects.equals(id, other.id);
		}




		public String trim() {
			return null;
		}
		
		
		

}
