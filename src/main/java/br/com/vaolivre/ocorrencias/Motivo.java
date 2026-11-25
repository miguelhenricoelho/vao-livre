package br.com.vaolivre.ocorrencias;

/** Por que a portaria recusou o pedido. O texto curto aparece no histórico e nos selos. */
public enum Motivo {
  VAGAS_ESGOTADAS("Vagas em uso"),
  CONTRATO_SUSPENSO("Contrato suspenso"),
  CONTRATO_VENCIDO("Contrato vencido"),
  CONTRATO_FUTURO("Contrato ainda não começou"),
  SEM_CONTRATO("Sem contrato vigente"),
  CLIENTE_INATIVO("Cliente desativado"),
  VEICULO_INATIVO("Veículo desativado"),
  PLACA_NAO_CADASTRADA("Placa não cadastrada"),
  JA_NO_PATIO("Já está no pátio"),
  FORA_DO_PATIO("Não está no pátio");

  private final String curto;

  Motivo(String curto) {
    this.curto = curto;
  }

  public String getCurto() {
    return curto;
  }

  /** Motivos em que faz sentido avisar o cliente pelo WhatsApp. */
  public boolean avisaCliente() {
    return this == VAGAS_ESGOTADAS
        || this == CONTRATO_SUSPENSO
        || this == CONTRATO_VENCIDO
        || this == CONTRATO_FUTURO;
  }
}
