package com.sourcepoint.cmplibrary.core.layout;

import android.content.Context;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sourcepoint.cmplibrary.ConsentLib;
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction;
import com.sourcepoint.cmplibrary.model.ActionType;

/**
 * TODO: document NativeMessage view class.
 */
public class NativeMessage extends RelativeLayout {

    public ActionButton cancel;
    public ActionButton acceptAll;
    public ActionButton rejectAll;
    public ActionButton showOptions;

    public TextView body;
    public TextView title;

    private NativeMessageClient client = null;

    public NativeMessage(Context context) {
        super(context);
        init();
    }

    public NativeMessage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NativeMessage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setId(View.generateViewId());
        }
//        inflate(getContext(), R.layout.sample_native_message, this);
//        setAcceptAll(findViewById(R.id.AcceptAll));
//        setRejectAll(findViewById(R.id.RejectAll));
//        setShowOptions(findViewById(R.id.ShowOptions));
//        setCancel(findViewById(R.id.Cancel));
//        setTitle(findViewById(R.id.Title));
//        setBody(findViewById(R.id.MsgBody));
    }

    public void setOnclickAction(ActionButton actionButton, ConsentLib consentLib){
//        actionButton.button.setOnClickListener(_v -> consentLib.onAction(new ConsentAction(actionButton.choiceType, String.valueOf(actionButton.choiceId),false, null)));
        actionButton.button.setOnClickListener(_v -> triggerAction(actionButton, consentLib));
    }

    private void triggerAction(ActionButton actionButton, ConsentLib consentLib){
        ConsentAction action = null;//new ConsentAction(actionButton.choiceType, String.valueOf(actionButton.choiceId),false, null);
//        consentLib.onAction(action);
        if(client != null){
            switch (ActionType.SHOW_OPTIONS/*valueOf(actionButton.choiceType)*/) {
                case SHOW_OPTIONS:
                    client.onClickShowOptions(action);
                    break;
                case PM_DISMISS:
                    client.onPmDismiss(action);
                    break;
                case MSG_CANCEL:
                    client.onClickCancel(action);
                    break;
                default:
                    client.onDefaultAction(action);
                    break;
            }
        }
    }

    public void setCallBacks(ConsentLib consentLib) {
        setOnclickAction(getAcceptAll(), consentLib);
        setOnclickAction(getRejectAll(), consentLib);
        setOnclickAction(getShowOptions(), consentLib);
        setOnclickAction(getCancel(), consentLib);
    }

    public void setAttributes(NativeMessageAttrs attrs){
        setChildAttributes(getTitle(), attrs.title);
        setChildAttributes(getBody(), attrs.body);
        for(NativeMessageAttrs.Action action: attrs.actions){
            setChildAttributes(findActionButton(action.choiceType), action);
        }
    }

    public ActionButton findActionButton(int choiceType){

        switch (ActionType.SHOW_OPTIONS/*ActionType.valueOf(choiceType)*/) {
            case SHOW_OPTIONS:
                return getShowOptions();
            case ACCEPT_ALL:
                return getAcceptAll();
            case MSG_CANCEL:
                return getCancel();
            case REJECT_ALL:
                return getRejectAll();
            default:
                return null;
        }
    }

    public void setChildAttributes(TextView v, NativeMessageAttrs.Attribute attr){
        v.setVisibility(View.VISIBLE);
        v.setText(attr.text);
        v.setTextColor(attr.style.color);
        v.setTextSize(attr.style.fontSize);
        v.setBackgroundColor(attr.style.backgroundColor);
    }

    public void setChildAttributes(ActionButton v, NativeMessageAttrs.Action attr){
        setChildAttributes(v.button, attr);
        v.choiceId = attr.choiceId;
        v.choiceType = attr.choiceType;
    }

    public ActionButton getCancel() {
        return cancel;
    }

    public void setCancel(Button cancel) {
        this.cancel = new ActionButton(cancel);
        this.cancel.button.setVisibility(View.INVISIBLE);
    }

    public ActionButton getAcceptAll() {
        return acceptAll;
    }

    public void setAcceptAll(Button acceptAll) {
        this.acceptAll = new ActionButton(acceptAll);
        this.acceptAll.button.setVisibility(View.INVISIBLE);
    }

    public ActionButton getRejectAll() {
        return rejectAll;
    }

    public void setRejectAll(Button rejectAll) {
        this.rejectAll = new ActionButton(rejectAll);
        this.rejectAll.button.setVisibility(View.INVISIBLE);
    }

    public ActionButton getShowOptions() {
        return showOptions;
    }

    public void setShowOptions(Button showOptions) {
        this.showOptions = new ActionButton(showOptions);
        this.showOptions.button.setVisibility(View.INVISIBLE);

    }

    public TextView getBody() {
        return body;
    }

    public void setBody(TextView body) {
        this.body = body;
        this.body.setVisibility(View.INVISIBLE);
        this.body.setMovementMethod(new ScrollingMovementMethod());
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
        this.title.setVisibility(View.INVISIBLE);
    }

    public static class ActionButton {

        public ActionButton(Button b){
            button = b;
        }

        public Button button;
        public int choiceType;
        public int choiceId;
    }

    public void setActionClient(NativeMessageClient pClient){
        client = pClient;
    }
}