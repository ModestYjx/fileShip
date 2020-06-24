package cn.flyingocean.fileship.service.impl;

import cn.flyingocean.fileship.domain.FileClaim;
import cn.flyingocean.fileship.repository.FileClaimRepository;
import cn.flyingocean.fileship.service.FileClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FileClaimServiceImpl implements FileClaimService{


    @Autowired
    private FileClaimRepository fileClaimRepo;

    @Override
    public void save(FileClaim fileClaim) {
        fileClaimRepo.save(fileClaim);
    }

    @Override
    public FileClaim findById(int id) {
        Optional<FileClaim> fileClaimOptional = fileClaimRepo.findById(id);
        if (fileClaimOptional.isPresent()){
            return fileClaimOptional.get();
        }else{
            return null;
        }
    }

    @Override
    public void deleteById(int id) {
        fileClaimRepo.deleteById(id);
    }

    @Override
    public FileClaim findByFileId(int fileId) {
        return fileClaimRepo.findByFileId(fileId);
    }


}
