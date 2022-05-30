package cn.itcast.item.service.impl;
import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.item.SearchItemDTO;
import cn.itcast.hmall.pojo.item.Item;
import cn.itcast.item.mapper.ItemMapper;
import cn.itcast.item.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED ,readOnly = false)
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {
    //分页
    @Transactional(propagation = Propagation.SUPPORTS ,readOnly = true)
    @Override
    public PageDTO<Item> getList(SearchItemDTO dto) {
        // TODO  补全其它条件
        Page<Item> result =this.page(new Page<>(dto.getPage(),dto.getSize()));
        return new PageDTO<Item>(result.getTotal(),result.getRecords());
    }
    //添加
    @Override
    public void addone(Item item) {
        item.setStatus(1);
        this.save(item);
    }
      //上下架
    @Override
    public void updateStatus(Long id, Integer status) {
        if(id==null||status==null){
            throw new RuntimeException("id不能为空或状态出错");
        }
        if(status==2||status==1){
            UpdateWrapper<Item> objectUpdateWrapper = new UpdateWrapper<>();
            objectUpdateWrapper.eq("id",id);
            objectUpdateWrapper.set("status",status);
            this.baseMapper.update(null,objectUpdateWrapper);
        }else{
            throw new RuntimeException("状态错误");
        }
    }
  //根据ID查询
    @Override
    public Item getOneItem(Long id) {
        if (id==null){
            throw  new RuntimeException("id不能为空");
        }
        return this.getById(id);
    }
      //修改
    @Override
    public void updateOne(Item item) {
         this.updateById(item);
    }
    //删除
    @Override
    public void deleteOne(Long id) {
        this.removeById(id);
    }
    //减少库存
    @Override
    public void stock(Long itemId, Integer num) {
        if(itemId!=null&&num!=null){
            UpdateWrapper<Item> itemUpdateWrapper = new UpdateWrapper<>();
            itemUpdateWrapper.eq("id",itemId).setSql("stock=stock-"+num);
            this.update(itemUpdateWrapper);
        }
    }
}
