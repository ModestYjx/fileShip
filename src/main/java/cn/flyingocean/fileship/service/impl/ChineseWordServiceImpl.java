package cn.flyingocean.fileship.service.impl;

import cn.flyingocean.fileship.domain.ChineseWord;
import cn.flyingocean.fileship.domain.File;
import cn.flyingocean.fileship.domain.Warehouse;
import cn.flyingocean.fileship.repository.ChineseWordRepository;
import cn.flyingocean.fileship.service.ChineseWordService;
import cn.flyingocean.fileship.service.FileService;
import cn.flyingocean.fileship.service.WareHouseService;
import cn.flyingocean.fileship.util.ChineseWordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChineseWordServiceImpl implements ChineseWordService {


    @Autowired
    ChineseWordRepository chineseWordRepo;
    @Autowired
    FileService fileService;
    @Autowired
    WareHouseService wareHouseService;


    @Value("${chineseWord.upperBottom}")
    private int chineseWordUpperBound;
    @Value("${chineseWord.lowerBottom}")
    private int chineseWordLowerBound;


    @Override
    public ChineseWord findById(int id) {
        Optional<ChineseWord> chineseWordOptional = chineseWordRepo.findById(id);
        if (chineseWordOptional.isPresent())    return chineseWordOptional.get();
        return null;
    }

    @Override
    public ChineseWord findByText(String text) {
        return chineseWordRepo.findByText(text);
    }

    @Override
    public ChineseWordUtil.Result getAvailableFileChineseToken(byte length) {
        SecureRandom secureRandom = new SecureRandom();
        boolean isContinue = true;
        StringBuilder sb = new StringBuilder();
        List<ChineseWord> list = new ArrayList<>();
        while (isContinue){
            int n = secureRandom.nextInt(chineseWordUpperBound+1);
            if (n<chineseWordLowerBound||n>chineseWordUpperBound) continue;
            ChineseWord chineseWord = findById(n);
            list.add(chineseWord);
            sb.append(chineseWord.getText());
            // 当达到指定长度，做一次有效性验证
            if (sb.length()==length){
                File file = fileService.findByChineseToken(sb.toString());
                if (file==null){
                    return new ChineseWordUtil.Result(list,sb.toString());
                }
                // 如果生成的ChineseToken已存在
                sb.delete(0,length);
                // fixme 重新指向一个新对象会不会比删除每一个元素要更快一点，或者在什么情况下该结论成立
                list = new ArrayList<>();
            }
        }
        return null;
    }

    @Override
    public ChineseWordUtil.Result getAvailableWarehouseChineseToken(byte length) {
        SecureRandom secureRandom = new SecureRandom();
        boolean isContinue = true;
        StringBuilder sb = new StringBuilder();
        List<ChineseWord> list = new ArrayList<>();
        while (isContinue){
            int n = secureRandom.nextInt(chineseWordUpperBound+1);
            if (n<chineseWordLowerBound||n>chineseWordUpperBound) continue;
            ChineseWord chineseWord = findById(n);
            list.add(chineseWord);
            sb.append(chineseWord.getText());
            // 当达到指定长度，做一次有效性验证
            if (sb.length()==length){
                Warehouse warehouse = wareHouseService.findByChineseToken(sb.toString());
                if (warehouse==null){
                    return new ChineseWordUtil.Result(list,sb.toString());
                }
                // 如果生成的ChineseToken已存在
                sb.delete(0,length);
                // fixme 重新指向一个新对象会不会比删除每一个元素要更快一点，或者在什么情况下该结论成立
                list = new ArrayList<>();
            }
        }
        return null;
    }
}
