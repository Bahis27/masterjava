package ru.javaops.masterjava.persist.testdata;

import java.util.List;

import com.google.common.collect.ImmutableList;

import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.ProjectGroupDao;
import ru.javaops.masterjava.persist.model.GroupType;
import ru.javaops.masterjava.persist.model.ProjectGroup;

public class ProjectGroupTestData {

    public static ProjectGroup MJ01;
    public static ProjectGroup TJ06;
    public static ProjectGroup TJ07;
    public static ProjectGroup TJ08;
    public static List<ProjectGroup> FIRST4_PROJECT_GROUPS;

    public static void init() {
        MJ01 = new ProjectGroup("masterjava01", GroupType.current, "masterjava");
        TJ06 = new ProjectGroup("topjava06", GroupType.finished, "topjava");
        TJ07 = new ProjectGroup("topjava07", GroupType.finished, "topjava");
        TJ08 = new ProjectGroup("topjava08", GroupType.current, "topjava");
        FIRST4_PROJECT_GROUPS = ImmutableList.of(MJ01, TJ06, TJ07, TJ08);
    }

    public static void setUp() {
        ProjectGroupDao dao = DBIProvider.getDao(ProjectGroupDao.class);
        dao.clean();
        DBIProvider.getDBI().useTransaction((conn, status) -> FIRST4_PROJECT_GROUPS.forEach(dao::insert));
    }
}
