package student.pl.edu.pb.geodeticapp.data.dao;

import java.util.List;

public interface GenericDAO<T> {
    T getByID(long id);
    List<T> getAll();
    long save(T entity);
    void update(T entity);
    void deleteByID(long id);
}
