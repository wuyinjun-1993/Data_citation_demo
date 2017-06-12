package edu.upenn.cis.citation.reasoning;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by gaoyan on 6/7/17.
 */
@RunWith(Arquillian.class)
public class Tuple_reasoning1Test {
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(Tuple_reasoning1.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

}
