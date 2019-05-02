package cc.creamcookie.jpa;

import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * @author eomjeongjae
 * @since 2019-02-22
 */
@Transactional(readOnly = true)
@Service
public class DataTablesService {

    private final ModelMapper modelMapper;


    public DataTablesService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <T> DataTablesOutput<T> convert(DataTablesOutput<? extends BaseEntity> source, Class<T> destinationType) {
        DataTablesOutput<T> output = new DataTablesOutput();
        output.setDraw(source.getDraw());
        output.setRecordsTotal(source.getRecordsTotal());
        output.setRecordsFiltered(source.getRecordsFiltered());
        output.setError(source.getError());
        output.setData(source.getData().stream()
                .map(post -> modelMapper.map(post, destinationType))
                .collect(Collectors.toList()));
        return output;
    }
}