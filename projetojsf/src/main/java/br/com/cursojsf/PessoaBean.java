package br.com.cursojsf;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;



import org.hibernate.service.spi.InjectService;

import com.google.gson.Gson;

import br.com.dao.DaoGeneric;
import br.com.entidades.Cidades;
import br.com.entidades.Estados;
import br.com.entidades.Pessoa;
import br.com.jpautil.JPAUtil;
import br.com.repository.IDaoPessoa;
import br.com.repository.IDaoPessoaImp;


//@ApplicationScoped       //TODOS OS USUÁRIOS compartilham os mesmos dados
//@SessionScoped 	      Dados mantidos na sessão do usuário
@ViewScoped         //Lista Morre quando redireciona a tela
//@RequestScoped      Como quer adicionar em uma lista, o request que é de vida curta não serve
@ManagedBean(name = "pessoaBean")  //referencia o nome e não a classe java
public class PessoaBean {

	private Pessoa pessoa = new Pessoa();
	private DaoGeneric<Pessoa> daoGeneric = new DaoGeneric<Pessoa>();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	private IDaoPessoa iDaoPessoa = new IDaoPessoaImp();
	
	private List<SelectItem> estados;
	private List<SelectItem> cidades;
	
	private Part arquivofoto;
	
	public String salvar() throws IOException {
		
		/*Processar imagem */
		byte[] imagemByte = getByte(arquivofoto.getInputStream());
		pessoa.setFotoIconBase64Original(imagemByte);  /* salva imagem original */
		/* transformar em bufferimage*/
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagemByte));
		/*pega o tipo da imagem*/
		int type = bufferedImage.getType() ==0? BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
		int largura = 200;
		int altura = 200;
		/*criar a miniatura */
		BufferedImage resizedImage = new BufferedImage(largura, altura, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(bufferedImage, 0, 0, largura, altura, null);
		g.dispose();
		/*Escrever novamente a imagem em tamanho menor*/
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String extensao = arquivofoto.getContentType().split("\\/")[1]; /*retorna - image/png */
		ImageIO.write(resizedImage, extensao, baos);
		String miniImagem = "data:"+arquivofoto.getContentType() +";base64," + DatatypeConverter.printBase64Binary(baos.toByteArray());
		/*Processar imagem */
		pessoa.setFotoIconBase64(miniImagem);
		pessoa.setExtensao(extensao);
		pessoa = daoGeneric.merge(pessoa);
		carregarPessoas();
		mostrarMsg("Cadastrado efetuado com sucesso!");
		return "";
	}
	
	public void registraLog() {
		System.out.println("Método registralog");
		/*actionListener nao redireciona para outra tela e é chamado antes do action*/
	}
	
	public void mudancaDeValor (ValueChangeEvent event) {
		System.out.println("valor antigo "+event.getOldValue());
		System.out.println("valor novo "+event.getNewValue());
		/* Um metodo para tratar cada Atributo - não pode ser o mesmo método
		 valueChangeListener Na hora que salva ele pega o valor antigo e o valor novo*/
	}
	
	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
	}

	public String novo(){
		pessoa=new Pessoa();
		return "";
	}
	
	public void pesquisaCep(AjaxBehaviorEvent event) {
		try {
			URL url = new URL("https://viacep.com.br/ws/"+pessoa.getCep()+"/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			String cep = "";
			StringBuilder jsonCep = new StringBuilder();
			while ((cep = br.readLine()) !=null) {
				jsonCep.append(cep);
			}
			//transforma o resultado para um objeto para auxiliar (pessoa.class tem que ter os mesmos atributos do json)
			Pessoa gson = new Gson().fromJson(jsonCep.toString(), Pessoa.class);
			//seta para o objeto controlado
			pessoa.setCep(gson.getCep());
			pessoa.setLogradouro(gson.getLogradouro());
			pessoa.setComplemento(gson.getComplemento());
			pessoa.setBairro(gson.getBairro());
			pessoa.setLocalidade(gson.getLocalidade());
			pessoa.setUf(gson.getUf());
			pessoa.setIbge(gson.getIbge());
			pessoa.setGia(gson.getGia());
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			mostrarMsg("Erro ao consultar o CEP");
		}
		
	}
	
	
	public String remove() {
		daoGeneric.deletePorId(pessoa);
		pessoa=new Pessoa();
		carregarPessoas();
		mostrarMsg("Removido com sucesso!");
		return "";
	}

	@PostConstruct
public void carregarPessoas() {
		pessoas = daoGeneric.getListEntity(Pessoa.class);
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public List<Pessoa> getPessoas() {
		return pessoas;
	}
	
	public String logar() {
		Pessoa pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());
		if(pessoaUser != null) {
			
			//adiciona o usuário na sessão usuarioLogado
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("usuarioLogado", pessoaUser);
			
			return "primeirapagina.xhtml";
		}
		return "index.xhtml";
		
	}
	
	public String deslogar() {
		
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("usuarioLogado");
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		httpServletRequest.getSession().invalidate();
		
		return "index.xhtml";
	}
	
	public boolean permiteAcesso(String acesso) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("usuarioLogado");
		return pessoaUser.getPerfilUser().equals(acesso);
	}
	
		
		public List<SelectItem> getEstados() {
			estados = iDaoPessoa.listaEstados();
			return estados;
		}
		
		public List<SelectItem> getCidades() {
			return cidades;
		}
		
		public void setCidades(List<SelectItem> cidades) {
			this.cidades = cidades;
		}
		
		
		public void carregaCidades(AjaxBehaviorEvent event) {
			Estados estado = (Estados)((HtmlSelectOneMenu)event.getSource()).getValue();
		
				if(estado != null) {
					pessoa.setEstados(estado);
					List<Cidades> cidades = JPAUtil.getEntityManager().createQuery("from Cidades where estados.id = " + estado.getId()).getResultList();
					List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();
					for (Cidades cidade : cidades) {
						selectItemsCidade.add(new SelectItem(cidade, cidade.getNome()));
					}
					setCidades(selectItemsCidade);
				}
			}
		
		public void editar() {
			if(pessoa.getCidades() != null) {
				Estados estado = pessoa.getCidades().getEstados();
				pessoa.setEstados(estado);
				List<Cidades> cidades = JPAUtil.getEntityManager().createQuery("from Cidades where estados.id = " + estado.getId()).getResultList();
				List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();
				for (Cidades cidade : cidades) {
					selectItemsCidade.add(new SelectItem(cidade, cidade.getNome()));
				}
				setCidades(selectItemsCidade);
			}
			}
		
		public void setArquivofoto(Part arquivofoto) {
			this.arquivofoto = arquivofoto;
		}
		
		public Part getArquivofoto() {
			return arquivofoto;
		}
		
		/*Método que converte um inputstream para um array de bytes */
		private byte[] getByte(InputStream is) throws IOException {
			int len;
			int size = 1024;
			byte[] buf = null;
			if(is instanceof ByteArrayInputStream) {
				size = is.available();
				buf = new byte[size];
				len = is.read(buf,0,size);
			}else {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				buf = new byte[size];
				while((len = is.read(buf,0,size)) != -1) {
					bos.write(buf, 0, len);
				}
				buf = bos.toByteArray();
			}
			return buf;
		}
		
		public void download() throws IOException {
			Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String fileDownloadId = params.get("fileDownloadId");
			Pessoa pessoa = daoGeneric.consultar(Pessoa.class, fileDownloadId);
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.addHeader("Content-Disposition", "attachment; filename=download."+pessoa.getExtensao());
			response.setContentType("application/octet-stream");
			response.setContentLength(pessoa.getFotoIconBase64Original().length);
			response.getOutputStream().write(pessoa.getFotoIconBase64Original());
			response.getOutputStream().flush();
			FacesContext.getCurrentInstance().responseComplete();
			
		}
			
		}
		
	

