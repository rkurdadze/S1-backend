package ge.studio101.service.services;

import ge.studio101.service.dto.SizeDTO;
import ge.studio101.service.mappers.SizeMapper;
import ge.studio101.service.repositories.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SizeService {
    @Autowired
    private final SizeRepository sizeRepository;

    @Autowired
    private final SizeMapper sizeMapper;

    public List<SizeDTO> findAll() {
        return sizeMapper.toDTOList(sizeRepository.findAllByOrderByIdAsc());
    }

    public SizeDTO findById(Long id) {
        return sizeRepository.findById(id)
                .map(sizeMapper::toDTO)
                .orElse(null);
    }
}
