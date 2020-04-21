package ru.javaops.masterjava.persist.testdata;

import java.util.List;

import com.google.common.collect.ImmutableList;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.ProjectDao;
import ru.javaops.masterjava.persist.model.Project;

public class ProjectTestData {

    public static Project MJ;
    public static Project TJ;
    public static List<Project> FIRST2_PROJECTS;

    public static void init() {
        MJ = new Project("masterjava", "Masterjava");
        TJ = new Project("topjava", "Topjava");
        FIRST2_PROJECTS = ImmutableList.of(MJ, TJ);
    }

    public static void setUp() {
        ProjectDao dao = DBIProvider.getDao(ProjectDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction(((conn, status) -> FIRST2_PROJECTS.forEach(dao::insert)));
    }
}
