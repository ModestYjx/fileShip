package cn.flyingocean.fileship.service.impl;

import cn.flyingocean.fileship.domain.Perm;
import cn.flyingocean.fileship.repository.PermRepository;
import cn.flyingocean.fileship.service.PermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermServiceImpl implements PermService{

    @Autowired
    PermRepository permRepo;

    @Override
    public Perm findById(int id) {
        Optional<Perm> permOptional = permRepo.findById(id);
        if (!permOptional.isPresent()){
            return null;
        }
        return permOptional.get();
    }
}
