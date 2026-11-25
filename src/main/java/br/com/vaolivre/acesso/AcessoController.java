package br.com.vaolivre.acesso;

import br.com.vaolivre.acesso.AcessoService.Conferencia;
import br.com.vaolivre.acesso.AcessoService.ResultadoDaArea;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** As duas portas da área da equipe: a senha da área e o login de cada pessoa. */
@Controller
public class AcessoController {

  private final AcessoService acesso;

  public AcessoController(AcessoService acesso) {
    this.acesso = acesso;
  }

  @GetMapping("/entrar")
  public String entrar(Model modelo) {
    modelo.addAttribute("tentativas", acesso.tentativasDaArea());
    modelo.addAttribute("minutos", acesso.minutosDeBloqueio());
    return "acesso/entrar";
  }

  @PostMapping("/entrar")
  public String conferir(
      @RequestParam(name = "senhaDaArea", required = false) String senha,
      HttpServletRequest pedido,
      Model modelo) {
    Conferencia conferencia = acesso.conferirSenhaDaArea(senha, pedido.getRemoteAddr());
    if (conferencia.resultado() == ResultadoDaArea.LIBERADA) {
      HttpSession antiga = pedido.getSession(false);
      if (antiga != null) {
        antiga.invalidate();
      }
      pedido.getSession(true).setAttribute(AcessoService.ATRIBUTO_DA_SESSAO, conferencia.versao());
      return "redirect:/login";
    }
    modelo.addAttribute("tentativas", acesso.tentativasDaArea());
    modelo.addAttribute("minutos", acesso.minutosDeBloqueio());
    modelo.addAttribute("erroDaArea", conferencia.resultado().name());
    return "acesso/entrar";
  }

  @GetMapping("/login")
  public String login() {
    return "acesso/login";
  }
}
