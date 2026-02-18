package vn.hoidanit.jobhunter.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCompanyDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.service.CompanyService;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);

    }

    @Override
    public Company handleUpdateCompany(Company reqCompany) {
        Optional<Company> comCheck = companyRepository.findById(reqCompany.getId());

        if (comCheck.isPresent()) {
            Company company = comCheck.get();
            company.setDescription(reqCompany.getDescription());
            company.setAddress(reqCompany.getAddress());
            company.setLogo(reqCompany.getLogo());
            company.setName(reqCompany.getName());

            return this.companyRepository.save(company);

        }
        return null;

    }

    @Override
    public ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable) {

        Page<Company> pageCom = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        ResultPaginationDTO result = new ResultPaginationDTO();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageCom.getTotalPages());
        meta.setTotal(pageCom.getTotalElements());

        result.setMeta(meta);
        // result.setResult(pageCom.getContent());
        result.setResult(pageCom.getContent()
                .stream().map(item -> new ResCompanyDTO(
                        item.getId(),
                        item.getName(),
                        item.getAddress(),
                        item.getLogo(),
                        item.getCreatedAt(),
                        item.getUpdatedAt()))
                .collect(Collectors.toList()));

        return result;
    }

    @Override
    public void handleDeleteCompany(Long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        if (company.isPresent()) {
            Company com = company.get();

            List<User> users = this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(users);
        }

        this.companyRepository.deleteById(id);

    }

    @Override
    public Optional<Company> findById(Long id) {
        return this.companyRepository.findById(id);
    }

}
