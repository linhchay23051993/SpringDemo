package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {

	private final CompanyRepository companyRepository;

	public CompanyService(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}
	
	public Company handleCreateCompany(Company company) {
		return companyRepository.save(company);
	}
	
	public ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable){
		Page<Company> pageUser = this.companyRepository.findAll(spec, pageable);
		ResultPaginationDTO rs =  new ResultPaginationDTO();
		Meta mt = new Meta();
		
		mt.setPage( pageable.getPageNumber()+1);
		mt.setPageSize(pageable.getPageSize());
		mt.setPages(pageUser.getTotalPages());
		mt.setTotal(pageUser.getTotalElements());
		
		rs.setMeta(mt);
		rs.setResult(pageUser.getContent());
		
		return rs;
	}
	
	public Company handleUpdateCompany(Company company) {
		Optional<Company> companyOptional = this.companyRepository.findById(company.getId());
		if(companyOptional.isPresent()) {
			Company currentCompany = companyOptional.get();
			currentCompany.setLogo(company.getLogo());
			currentCompany.setName(company.getName());
			currentCompany.setDescription(company.getDescription());
			currentCompany.setAddress(company.getAddress());
			return this.companyRepository.save(currentCompany);
			
		}
		return null;
	}
	
	public void handleDeleteCompany(long id) {
		 companyRepository.deleteById(id);;
	}
}
