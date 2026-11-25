package br.com.vaolivre.auditoria;

import br.com.vaolivre.equipe.UsuariosRepository;
import br.com.vaolivre.global.Formatos;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Registra e lista as ações administrativas. */
@Service
public class AuditoriaService {

  private static final int POR_PAGINA = 30;

  private final AuditoriaRepository registros;
  private final UsuariosRepository usuarios;
  private final Clock relogio;

  public AuditoriaService(
      AuditoriaRepository registros, UsuariosRepository usuarios, Clock relogio) {
    this.registros = registros;
    this.usuarios = usuarios;
    this.relogio = relogio;
  }

  /** Grava dentro da mesma transação da ação: se a ação falhar, o registro também não fica. */
  @Transactional(propagation = Propagation.MANDATORY)
  public void registrar(long usuarioId, String acao, String entidade, Long id, String resumo) {
    registros.save(
        new RegistroAuditoria(
            usuarios.getReferenceById(usuarioId),
            acao,
            entidade,
            id,
            resumo,
            OffsetDateTime.now(relogio)));
  }

  @Transactional(readOnly = true)
  public Page<LinhaAuditoria> listar(int pagina) {
    return registros
        .findAllByOrderByOcorridoEmDesc(PageRequest.of(Math.max(pagina, 0), POR_PAGINA))
        .map(r ->
            new LinhaAuditoria(
                Formatos.dataHora(r.getOcorridoEm(), relogio),
                r.getUsuario().getNome(),
                r.getAcao(),
                r.getResumo()));
  }

  public record LinhaAuditoria(String quando, String quem, String acao, String resumo) {}
}
