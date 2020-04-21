package ru.javaops.masterjava.persist.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.javaops.masterjava.persist.model.Project;
import ru.javaops.masterjava.persist.testdata.ProjectTestData;

import static ru.javaops.masterjava.persist.testdata.ProjectTestData.FIRST2_PROJECTS;

public class ProjectDaoTest extends AbstractDaoTest<ProjectDao> {

    public ProjectDaoTest() {
        super(ProjectDao.class);
    }

    @BeforeClass
    public static void init() throws Exception {
        ProjectTestData.init();
    }

    @Before
    public void setUp() throws Exception {
        ProjectTestData.setUp();
    }

    @Test
    public void getWithLimit() {
        List<Project> projects = dao.getWithLimit(2);
        Assert.assertEquals(FIRST2_PROJECTS, projects);
    }

    @Test
    public void insertBatch() {
        dao.clean();
        dao.insertBatch(FIRST2_PROJECTS, 2);
        Assert.assertEquals(2, dao.getWithLimit(100).size());
        Assert.assertEquals(FIRST2_PROJECTS, dao.getWithLimit(2));
    }

}
