package com.dormhub.dao;

public interface DormPassDAO {
    void insert(DormPass dormPass);

    void update(DormPass dormPass);

    void delete(int passId);

    DormPass findById(int passId);

    List<DormPass> findByResidentId(int residentId);

    List<DormPass> findByStatus(String status);

    List<DormPass> findByType(String type);

    List<DormPass> findAllDormPasses();
}
