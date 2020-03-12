package com.testbank.money.di;

import org.junit.Assert;
import org.junit.Test;

public class ContextTest {

  static class AdHocService implements Service {};

  @Test
  public void mustGiveContextInstance() {
    /*
     * Then
     */
    Assert.assertNotNull(Context.get());
  }

  @Test
  public void mustRegisterAndProvideSingletonBean() {
    /*
     * Given
     */
    final Service serviceMock = new Service() {};

    /*
     * When
     */
    Context.get().registerSingleton("test-name", serviceMock);
    final Service serviceGot = Context.get().getInstance("test-name");

    /*
     * Then
     */
    Assert.assertSame(serviceMock, serviceGot);
  }

  @Test
  public void mustRegisterAndProvideNonSingletonBean() {
    /*
     * Given
     */

    /*
     * When
     */
    Context.get().register("test-name", AdHocService.class);
    final Service serviceGot1 = Context.get().getInstance("test-name");
    final Service serviceGot2 = Context.get().getInstance("test-name");

    /*
     * Then
     */
    Assert.assertNotSame(serviceGot1, serviceGot2);
  }

  @Test(expected = UnknownBeanException.class)
  public void mustFailDueToUnknownBean() {
    /*
     * Given
     */
    final Service serviceMock = new Service() {};
    Context.get().registerSingleton("test-name", serviceMock);

    /*
     * When
     */
    Assert.assertNotNull(Context.get().getInstance("nonexisting-bean"));

    /*
     * Then
     *
     * Must fail.
     */

  }

  @Test(expected = InstantiationException.class)
  public void mustFailDueToInstantiationProblem() {
    /*
     * Given
     */
    class BrokenService implements Service {
      public BrokenService() {
        throw new RuntimeException("Fake exception");
      }
    };
    Context.get().register("test-name", BrokenService.class);

    /*
     * When
     */
    Context.get().getInstance("test-name");

    /*
     * Then
     *
     * Must fail.
     */

  }
}
