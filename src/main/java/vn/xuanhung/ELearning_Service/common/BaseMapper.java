package vn.xuanhung.ELearning_Service.common;

import org.modelmapper.ModelMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseMapper<E, D> {
    private final Class<E> entityClass;
    private final Class<D> dtoClass;
    protected ModelMapper modelMapper;

    protected BaseMapper(Class<E> entityClass, Class<D> dtoClass, ModelMapper modelMapper) {
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
        this.modelMapper = modelMapper;
    }

    public E convertToEntity(D dto) {
        return modelMapper.map(dto, entityClass);
    }

    public D convertToDto(E entity) {
        return modelMapper.map(entity, dtoClass);
    }

    public List<E> convertToEntityList(Collection<D> dtoList) {
        return dtoList.stream().map(this::convertToEntity).collect(Collectors.toList());
    }

    public List<D> convertToDtoList(Collection<E> entityList) {
        return entityList.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}

