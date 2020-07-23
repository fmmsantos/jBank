package dev.estudos.jbank.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.hibernate.id.IncrementGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.estudos.jbank.dto.SolicitacaoEmprestimoDTO;
import dev.estudos.jbank.exception.BusinessException;
import dev.estudos.jbank.exception.PagamentoNaoAceitoException;
import dev.estudos.jbank.model.Cliente;
import dev.estudos.jbank.model.Configuracao;
import dev.estudos.jbank.model.Emprestimo;
import dev.estudos.jbank.model.Parcela;
import dev.estudos.jbank.model.StatusEmprestimo;
import dev.estudos.jbank.model.StatusParcela;
import dev.estudos.jbank.repository.ClienteRepository;
import dev.estudos.jbank.repository.ConfiguracaoRepository;
import dev.estudos.jbank.repository.EmprestimoRepository;
import dev.estudos.jbank.repository.ParcelaRepository;
import dev.estudos.jbank.utils.FlexibleCalendar;
import net.bytebuddy.build.Plugin.Engine.Source.Empty;

@Service
public class EmprestimoServiceImpl implements EmprestimoService {

	@Autowired
	private EmprestimoRepository repository;
	@Autowired
	private ClienteRepository repoCliente;

	@Autowired
	private ConfiguracaoRepository config;

	@Autowired
	private ParcelaRepository parcelaRepository;

	@Override
	public Emprestimo simular(SolicitacaoEmprestimoDTO solicitacao) {
		if (solicitacao.getCpfCnpjCliente() == null) {
			throw new IllegalArgumentException("Cliente não Registrado. Porfavor, realizar o cadastramento");
		}

		Emprestimo emprestimo = new Emprestimo();
		Configuracao configuracao = config.getConfiguracao();

		if (solicitacao != null) {

			Cliente cliente = repoCliente.findBycpfCnpj(solicitacao.getCpfCnpjCliente());
			if (cliente == null) {
				throw new IllegalArgumentException(
						"Cliente não localizado com este cpf " + solicitacao.getCpfCnpjCliente());
			}

			emprestimo.setCliente(cliente);
			emprestimo.setTaxaJuros(cliente.getTaxaJurosAoMes());

			if (cliente.getTaxaJurosAoMes() == null) {
				emprestimo.setTaxaJuros(configuracao.getTaxaJurosPadrao());
			}

			emprestimo.setValorSolicitado(solicitacao.getValorSolicitado());

			emprestimo.setTotalJuros(emprestimo.getValorSolicitado().multiply(emprestimo.getTaxaJuros())
					.multiply(new BigDecimal(solicitacao.getQtdParcelas()))
					.divide(new BigDecimal(100), RoundingMode.HALF_EVEN));

			if (!(configuracao.getQtdMaximaParcelas() == 0)) {
				if (solicitacao.getQtdParcelas().compareTo(configuracao.getQtdMaximaParcelas()) > 0) {

					throw new BusinessException("Quantidade de parcelas maior que a maxima permitida");
				}

			}

			emprestimo.setDataHoraSolicitacao(FlexibleCalendar.currentDateTime());
			emprestimo.setTotalAPagar(solicitacao.getValorSolicitado().add(emprestimo.getTotalJuros()));

			List<Parcela> parcelas = new ArrayList<>();
			for (int i = 0; i < solicitacao.getQtdParcelas(); i++) {
				Parcela parcela = new Parcela();
				parcela.setNumero(i + 1);
				parcela.setDataVencimento(FlexibleCalendar.currentDate().plusMonths(1L + i));

				parcela.setValorPrincipal(emprestimo.getValorSolicitado()
						.divide(new BigDecimal(solicitacao.getQtdParcelas()), 2, RoundingMode.HALF_EVEN));

				if (!(configuracao.getValorMinimoParcela() == configuracao.getValorMinimoParcela().ZERO)) {
					if (parcela.getValorPrincipal().compareTo(configuracao.getValorMinimoParcela()) < 0) {
						throw new BusinessException("Valor da parcela menor que o minimo permitido");

					}
				}

				parcela.setValorJuros(
						emprestimo.getTotalJuros().divide(new BigDecimal(solicitacao.getQtdParcelas()), 2));
				parcela.setValorTotal(emprestimo.getTotalAPagar().divide(new BigDecimal(solicitacao.getQtdParcelas()),
						2, RoundingMode.HALF_EVEN));
				parcelas.add(parcela);

			}
			emprestimo.setParcelas(parcelas);

		}
		boolean aprovar = aprovar(solicitacao, emprestimo);

		if (aprovar) {

		}

		// repository.save(emprestimo);
		return emprestimo;
	}

	@Override
	public Emprestimo contratar(SolicitacaoEmprestimoDTO solicitacao) {

		if (solicitacao.getCpfCnpjCliente() == null) {
			throw new IllegalArgumentException("Cliente não Registrado. Porfavor, realizar o cadastramento");
		}

		Emprestimo emprestimo = new Emprestimo();
		Configuracao configuracao = config.getConfiguracao();

		if (solicitacao != null) {

			Cliente cliente = repoCliente.findBycpfCnpj(solicitacao.getCpfCnpjCliente());
			if (cliente == null)
				throw new IllegalArgumentException(
						"Cliente não localizado com este cpf " + solicitacao.getCpfCnpjCliente());

			emprestimo.setCliente(cliente);
			emprestimo.setTaxaJuros(cliente.getTaxaJurosAoMes());

			if (cliente.getTaxaJurosAoMes() == null) {
				emprestimo.setTaxaJuros(configuracao.getTaxaJurosPadrao());
			}

			emprestimo.setValorSolicitado(solicitacao.getValorSolicitado());

			emprestimo.setTotalJuros(emprestimo.getValorSolicitado().multiply(emprestimo.getTaxaJuros())
					.multiply(new BigDecimal(solicitacao.getQtdParcelas()))
					.divide(new BigDecimal(100), RoundingMode.HALF_EVEN));
			if (!(configuracao.getQtdMaximaParcelas() == 0)) {
				if (solicitacao.getQtdParcelas().compareTo(configuracao.getQtdMaximaParcelas()) > 0) {

					throw new BusinessException("Quantidade de parcelas maior que a maxima permitida");
				}

			}

			emprestimo.setDataHoraSolicitacao(FlexibleCalendar.currentDateTime());
			emprestimo.setTotalAPagar(solicitacao.getValorSolicitado().add(emprestimo.getTotalJuros()));

			List<Parcela> parcelas = new ArrayList<>();
			for (int i = 0; i < solicitacao.getQtdParcelas(); i++) {
				Parcela parcela = new Parcela();
				parcela.setNumero(i + 1);
				parcela.setDataVencimento(FlexibleCalendar.currentDate().plusMonths(1L + i));

				parcela.setValorPrincipal(emprestimo.getValorSolicitado()
						.divide(new BigDecimal(solicitacao.getQtdParcelas()), 2, RoundingMode.HALF_EVEN));

				if (!(configuracao.getValorMinimoParcela() == configuracao.getValorMinimoParcela().ZERO)) {
					if (parcela.getValorPrincipal().compareTo(configuracao.getValorMinimoParcela()) < 0) {
						throw new BusinessException("Valor da parcela menor que o minimo permitido");

					}
				}

				parcela.setValorJuros(
						emprestimo.getTotalJuros().divide(new BigDecimal(solicitacao.getQtdParcelas()), 2));
				parcela.setValorTotal(emprestimo.getTotalAPagar().divide(new BigDecimal(solicitacao.getQtdParcelas()),
						2, RoundingMode.HALF_EVEN));
				parcela.setStatus(StatusParcela.A_VENCER);
				parcelas.add(parcela);

			}
			emprestimo.setParcelas(parcelas);

		}
		boolean aprovar = aprovar(solicitacao, emprestimo);

		if (aprovar) {

		}

		repository.save(emprestimo);

		return emprestimo;
	}

	public boolean aprovar(SolicitacaoEmprestimoDTO solicitacao, Emprestimo emprestimo) {
		Configuracao configuracao = config.getConfiguracao();

		if (emprestimo.getCliente().getDataNascimento() != null) {

			int idade = calcularIdade(emprestimo.getCliente().getDataNascimento());

			if (idade < configuracao.idadeMinima) {
				emprestimo.setStatus(StatusEmprestimo.REJEITADO);
				emprestimo.setObservacao("CLIENTE COM IDADE MENOR QUE A MINIMA PERMITIDA: " + "idade minima: "
						+ configuracao.idadeMinima + " anos," + " idade do cliente: " + idade + " anos.");
				return true;
			}

		}

		Parcela parcela = emprestimo.getParcelas().get(0);
		if (emprestimo.getCliente().getLimiteCredito().compareTo(solicitacao.getValorSolicitado()) >= 0) {
			emprestimo.setObservacao("LIMITE DE CRÉDITO ATENDE AO VALOR SOLICITADO");
			emprestimo.setDataHoraAprovacao(FlexibleCalendar.currentDateTime());
			emprestimo.setStatus(StatusEmprestimo.APROVADO);

			emprestimo.getTotalAPagar();
			return true;

		} else if (emprestimo.getCliente().getRendaMensal()
				.compareTo(parcela.getValorTotal().multiply(new BigDecimal(3))) > 0) {
			emprestimo.setObservacao("RENDA MENSAL DO CLIENTE 3X SUPERIOR A PARCELA");
			emprestimo.setDataHoraAprovacao(FlexibleCalendar.currentDateTime());
			emprestimo.setStatus(StatusEmprestimo.APROVADO);

			emprestimo.getTotalAPagar();
			return true;

		}

		else if ((emprestimo.getCliente().getLimiteCredito().compareTo(solicitacao.getValorSolicitado()) < 0
				&& (emprestimo.getCliente().getLimiteCredito().compareTo(new BigDecimal(0)) > 0))) {

			emprestimo.setObservacao("LIMITE DE CRÉDITO NÃO ATENDE O VALOR SOLICITADO");
			emprestimo.setDataHoraRejeicao(FlexibleCalendar.currentDateTime());
			emprestimo.setStatus(StatusEmprestimo.REJEITADO);
			return false;
		} else if (emprestimo.getCliente().getLimiteCredito().compareTo(new BigDecimal(0)) == 0) {

			emprestimo.setStatus(StatusEmprestimo.EM_ANALISE);
			return true;
		}

		return false;

	}

	@Override
	public boolean aprovar(Long idEmprestimo, String motivo) {
		if (idEmprestimo == null || motivo.isEmpty()) {
			throw new IllegalArgumentException("idEmprestimo e motivo devem ser informados");
		}

		Optional<Emprestimo> busca = repository.findById(idEmprestimo);

		if (!busca.isPresent()) {
			throw new IllegalArgumentException("Emprestimo não encontrado com o id " + idEmprestimo);
		}

		Emprestimo emprestimo = busca.get();

		if (emprestimo.getStatus() == StatusEmprestimo.APROVADO) {
			throw new BusinessException("Somente emprestimos em analise podem ser aprovados manualmente");
		}

		if (emprestimo.getStatus() == StatusEmprestimo.REJEITADO) {
			throw new BusinessException("Somente emprestimos em analise podem ser aprovados manualmente");
		}

		if (emprestimo.getStatus() == StatusEmprestimo.EM_ANALISE) {
			emprestimo.setStatus(StatusEmprestimo.APROVADO);
			emprestimo.setObservacao(motivo);
			repository.save(emprestimo);

			return true;
		}
		return false;
	}

	@Override
	public boolean rejeitar(Long idEmprestimo, String motivo) {
		if (idEmprestimo == null || motivo.isEmpty()) {
			throw new IllegalArgumentException("idEmprestimo e motivo devem ser informados");
		}

		Optional<Emprestimo> busca = repository.findById(idEmprestimo);

		if (!busca.isPresent()) {
			throw new IllegalArgumentException("Emprestimo não encontrado com o id " + idEmprestimo);
		}

		Emprestimo emprestimo = busca.get();

		if (emprestimo.getStatus() == StatusEmprestimo.APROVADO) {
			throw new BusinessException("Somente emprestimos em analise podem ser aprovados manualmente");
		}

		if (emprestimo.getStatus() == StatusEmprestimo.REJEITADO) {
			throw new BusinessException("Somente emprestimos em analise podem ser aprovados manualmente");
		}

		if (emprestimo.getStatus() == StatusEmprestimo.EM_ANALISE) {
			emprestimo.setStatus(StatusEmprestimo.REJEITADO);
			emprestimo.setObservacao(motivo);
			repository.save(emprestimo);

			return true;
		}
		return false;
	}

	private Integer calcularIdade(LocalDate dataNascimento) {
		LocalDate dataDate = LocalDate.from(dataNascimento);
		int hoje = FlexibleCalendar.currentDate().getYear();
		int idade = hoje - dataDate.getYear();

		return idade;
	}

	@Override
	@Transactional
	public Parcela processarStatusParcela(Long idEmprestimo, int numeroParcela) {

		Optional<Emprestimo> emprestimo = repository.findById(idEmprestimo);
		LocalDate incrementarData = null;
		Parcela parcela = getParcela(idEmprestimo,numeroParcela);
		
		if (!emprestimo.isPresent()) {
			throw new IllegalArgumentException("Emprestimo nao encontrado com o id " + idEmprestimo);
		}

		if (parcela == null) {
			throw new IllegalArgumentException("Parcela nao encontrado com o id ");
		}

		if (parcela.getDataVencimento().isBefore(FlexibleCalendar.currentDate())) {
			parcela.setStatus(StatusParcela.EM_ATRASO);

		}
		if (parcela.getDataVencimento().getDayOfWeek() == DayOfWeek.SATURDAY) {
			incrementarData = parcela.getDataVencimento().plusDays(2);
			parcela.setDataVencimento(incrementarData);
			if (parcela.getDataVencimento().isBefore(FlexibleCalendar.currentDate())) {
				parcela.setStatus(StatusParcela.EM_ATRASO);
			} else {
				parcela.setStatus(StatusParcela.A_VENCER);
			}
		}
		if	(parcela.getDataVencimento().getDayOfWeek() == DayOfWeek.SUNDAY) {
			incrementarData = parcela.getDataVencimento().plusDays(1);
			parcela.setDataVencimento(incrementarData);
			
			if (parcela.getDataVencimento().isBefore(FlexibleCalendar.currentDate())) {
				parcela.setStatus(StatusParcela.EM_ATRASO);
			} else {
				parcela.setStatus(StatusParcela.A_VENCER);
			}
		}

		return parcela;
	}

	@Override
	public Parcela getParcela(Long idEmprestimo, int numParcela) {
		Parcela parcela = parcelaRepository.findByEmprestimoIdAndNumero(idEmprestimo, numParcela);
		return parcela;
	}

}
