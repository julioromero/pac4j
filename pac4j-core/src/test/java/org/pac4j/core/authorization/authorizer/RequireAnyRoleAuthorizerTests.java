package org.pac4j.core.authorization.authorizer;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link RequireAnyRoleAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class RequireAnyRoleAuthorizerTests {

    private static final String ROLE1 = "role1";
    private static final String ROLE2 = "role2";
    private static final String ROLE3 = "role3";

    private final MockWebContext context = MockWebContext.create();

    private List<UserProfile> profiles;

    private CommonProfile profile;

    @Before
    public void setUp() {
        profile = new CommonProfile();
        profiles = new ArrayList<>();
        profiles.add(profile);
    }

    @Test
    public void testHasAnyRoleOneRole() {
        val authorizer = new RequireAnyRoleAuthorizer(ROLE1);
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyRoleOneRole2() {
        val authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(ROLE1);
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyRoleOneRoleTwoProfiles() {
        val authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(ROLE1);
        profile.addRole(ROLE1);
        profiles.add(new CommonProfile());
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyRoleOneRole3() {
        val authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(Arrays.asList(ROLE1));
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyRoleOneRole4() {
        val authorizer = new RequireAnyRoleAuthorizer();
        authorizer.setElements(new HashSet<>(Arrays.asList(ROLE1)));
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyRoleOneRoleFail() {
        val authorizer = new RequireAnyRoleAuthorizer(new String[] {ROLE1});
        profile.addRole(ROLE2);
        assertFalse(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyRoleNull() {
        val authorizer = new RequireAnyRoleAuthorizer((List<String>) null);
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyRoleEmpty() {
        val authorizer = new RequireAnyRoleAuthorizer(new String[] {});
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyRoleOkTwoRoles() {
        val authorizer = new RequireAnyRoleAuthorizer(ROLE2, ROLE1);
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyRoleProfileTwoRolesFail() {
        val authorizer = new RequireAnyRoleAuthorizer(new String[] {ROLE2});
        profile.addRole(ROLE1);
        profile.addRole(ROLE3);
        assertFalse(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyRoleAtLeastOneFails() {
        val authorizer = new RequireAnyRoleAuthorizer();
        assertFalse(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }

    @Test
    public void testHasAnyRoleAtLeastOneOk() {
        val authorizer = new RequireAnyRoleAuthorizer();
        profile.addRole(ROLE1);
        assertTrue(authorizer.isAuthorized(context, new MockSessionStore(), profiles));
    }
}
