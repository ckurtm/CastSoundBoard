package mbanje.kurt.soundboard;

import android.content.Context;

import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Kurt
 * @since 2017/01/31.
 */

public class CastOptionsProvider implements OptionsProvider {

    //This is the custom namespace we will use to communicate with the cast receiver application
    public static final String NAMESPACE = "urn:x-cast:mbanje.kurt.soundboard";

    @Override
    public CastOptions getCastOptions(Context context) {

        List<String> supportedNamespaces = new ArrayList<>();
        supportedNamespaces.add(NAMESPACE);
        CastOptions castOptions = new CastOptions.Builder()
                .setReceiverApplicationId(context.getString(R.string.app_id))
//                .setSupportedNamespaces(supportedNamespaces)
                .build();
        return castOptions;
    }
    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }
}