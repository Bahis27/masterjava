package ru.javaops.masterjava.persist.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.javaops.masterjava.persist.model.ProjectGroup;
import ru.javaops.masterjava.persist.testdata.ProjectGroupTestData;

import static ru.javaops.masterjava.persist.testdata.ProjectGroupTestData.FIRST4_PROJECT_GROUPS;

public class ProjectGroupDaoTest extends AbstractDaoTest<ProjectGroupDao> {

    public ProjectGroupDaoTest() {
        super(ProjectGroupDao.class);
    }

    @BeforeClass
    public static void init() throws Exception {
        ProjectGroupTestData.init();
    }

    @Before
    public void setUp() throws Exception {
        ProjectGroupTestData.setUp();
    }

    @Test
    public void getWithLimit() {
        List<ProjectGroup> projectGroups = dao.getWithLimit(4);
        Assert.assertEquals(FIRST4_PROJECT_GROUPS, projectGroups);
    }

    @Test
    public void insertBatch() {
        dao.clean();
        dao.insertBatch(FIRST4_PROJECT_GROUPS, 4);
        Assert.assertEquals(4, dao.getWithLimit(100).size());
        Assert.assertEquals(FIRST4_PROJECT_GROUPS, dao.getWithLimit(4));
    }

}
