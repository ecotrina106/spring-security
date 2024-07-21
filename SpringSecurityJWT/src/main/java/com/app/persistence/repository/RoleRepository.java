package com.app.persistence.repository;

import com.app.persistence.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity,Long> {

    //Se envia listado de string y se retorna solo los que conincidan en la base de datos
    List<RoleEntity> findRoleEntitiesByRoleEnumIn(List<String> rolesName);
}
