package org.pac4j.oauth.profile.vk;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.DateConverter;
import org.pac4j.core.profile.converter.GenderConverter;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.util.Arrays;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the Vk profile definition.
 *
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 */
public class VkProfileDefinition extends OAuthProfileDefinition {

    public static final String LAST_NAME = "last_name";
    public static final String SEX = "sex";
    public static final String BIRTH_DATE = "bdate";
    public static final String PHOTO_50 = "photo_50";
    public static final String PHOTO_100 = "photo_100";
    public static final String PHOTO_200_ORIG = "photo_200_orig";
    public static final String PHOTO_200 = "photo_200";
    public static final String PHOTO_400_ORIG = "photo_400_orig";
    public static final String PHOTO_MAX = "photo_max";
    public static final String PHOTO_MAX_ORIG = "photo_max_orig";
    public static final String ONLINE = "online";
    public static final String ONLINE_MOBILE = "online_mobile";
    public static final String DOMAIN = "domain";
    public static final String HAS_MOBILE = "has_mobile";
    public static final String MOBILE_PHONE = "mobile_phone";
    public static final String HOME_PHONE = "home_phone";
    public static final String SKYPE = "skype";
    public static final String SITE = "site";
    public static final String CAN_POST = "can_post";
    public static final String CAN_SEE_ALL_POST = "can_see_all_posts";
    public static final String CAN_SEE_AUDIO = "can_see_audio";
    public static final String CAN_WRITE_PRIVATE_MESSAGE = "can_write_private_message";
    public static final String STATUS = "status";
    public static final String COMMON_COUNT = "common_count";
    public static final String RELATION = "relation";

    protected final static String BASE_URL = "https://api.vk.com/method/users.get";

    public VkProfileDefinition() {
        super(x -> new VkProfile());
        Arrays.stream(new String[] {LAST_NAME, PHOTO_50, PHOTO_100, PHOTO_200_ORIG, PHOTO_200, PHOTO_400_ORIG,
                PHOTO_MAX, PHOTO_MAX_ORIG, DOMAIN, MOBILE_PHONE, HOME_PHONE, SKYPE, SITE, STATUS})
                .forEach(a -> primary(a, Converters.STRING));
        primary(COMMON_COUNT, Converters.INTEGER);
        primary(RELATION, Converters.INTEGER);
        Arrays.stream(new String[] {ONLINE, ONLINE_MOBILE, HAS_MOBILE, CAN_POST, CAN_SEE_ALL_POST, CAN_SEE_AUDIO,
            CAN_WRITE_PRIVATE_MESSAGE})
            .forEach(a -> primary(a, Converters.BOOLEAN));
        primary(BIRTH_DATE, new DateConverter("dd.MM.yyyy"));
        primary(SEX, new GenderConverter("2", "1"));
    }

    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return BASE_URL + "?fields=" + ((VkConfiguration) configuration).getFields();
    }

    @Override
    public VkProfile extractUserProfile(final String body) {
        val profile = (VkProfile) newProfile();
        var json = JsonHelper.getFirstNode(body);
        if (json != null) {
            var array = (ArrayNode) json.get("response");
            var userNode = array.get(0);
            if (userNode == null) {
                raiseProfileExtractionJsonError(body, "response");
            }
            profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(userNode, "uid")));
            for (val attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(userNode, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
