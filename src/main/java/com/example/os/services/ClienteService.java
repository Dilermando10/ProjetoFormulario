package com.example.os.services;


import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.os.domain.Cliente;
import com.example.os.domain.Pessoa;
import com.example.os.dtos.ClienteDTO;
import com.example.os.repositories.ClienteRepository;
import com.example.os.repositories.PessoaRepository;
import com.example.os.services.exceptions.DataIntegratyViolationException;
import com.example.os.services.exceptions.ObjectNotFoundException;



@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repository;
	
	@Autowired
	private PessoaRepository pessoaRepository;

	// BUSCA POR ID
	public Cliente findById(Integer id) {
		Optional<Cliente> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não entcontrado! ID: " + id + ", Tipo: " + Cliente.class.getName()));
	}

	// BUSCAR TODOS
	public List<Cliente> findAll() {
		return repository.findAll();
	}
	
	// CRIANDO CLIENTE
	public Cliente create(ClienteDTO objDTO) {
		if (findByCPF(objDTO) != null) {
			throw new DataIntegratyViolationException("CPF já cadastrado na base de dados!");
		}
		return repository.save(new Cliente(null, objDTO.getNome(), objDTO.getCpf(), objDTO.getTelefone()));
	}
	
	// ATUALIZANDO CLIENTE
	public Cliente update(Integer id, @Valid ClienteDTO objDTO) {
		Cliente oldObj = findById(id);
		if (findByCPF(objDTO) != null && findByCPF(objDTO).getId() != id) {
			throw new DataIntegratyViolationException("CPF já cadastrado na base de dados!");
		}
		
		oldObj.setNome(objDTO.getNome());
		oldObj.setCpf(objDTO.getCpf());
		oldObj.setTelefone(objDTO.getTelefone());
		
		return repository.save(oldObj);
	}
	
	// DELETAR CLIENTE
	public void deletar(Integer id) {
		Cliente obj = findById(id);
		if (obj.getList().size() > 0) {
			throw new DataIntegratyViolationException("Pessoa possui Ordem de servico, não pode ser deletada");
		}
		repository.deleteById(id);
	}
	
	// VALIDANDO SE EXISTE CPF CADASTRADO
	private Pessoa findByCPF(ClienteDTO objDTO) {
		Pessoa obj = pessoaRepository.findByCPF(objDTO.getCpf());
		if (obj != null) {
			return obj;
		}
		return null;
	}
}