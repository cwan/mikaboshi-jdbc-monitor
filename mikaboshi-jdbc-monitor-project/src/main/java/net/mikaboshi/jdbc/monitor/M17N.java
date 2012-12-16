package net.mikaboshi.jdbc.monitor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.mikaboshi.util.ResourceBundleWrapper;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * <p>
 * メッセージ、ラベルの多言語化を行う。
 * </p><p>
 * メッセージは、リソースバンドル（プロパティファイル）に定義されたものを読み込む。
 * プロパティファイルのベース名は、「net.mikaboshi.jdbc.monitor.language」である。
 * </p><p>
 * {@link #get(String)}, {@link #get(String, Object...)}メソッドで
 * 明示的に言語データを取得することができる。
 * </p><p>
 * また、AWTやSwingのフレームタイトル、ラベル、ボタンのテキスト等にメッセージIDが
 * 埋め込まれていた場合、AspectJにより自動的に言語データを設定する。
 * </p>
 * @author Takuma Umezawa
 *
 */
@Aspect
public class M17N {
	
	/** メッセージリソースプロパティファイルのベース名 */
	private static final String BASE_NAME = "net.mikaboshi.jdbc.monitor.language";
	
	private static Log logger = LogFactory.getLog(M17N.class);
	
	private static ResourceBundleWrapper bundle;
	
	static {
		try {
			ResourceBundle rb =
				ResourceBundle.getBundle(BASE_NAME);
			bundle = new ResourceBundleWrapper(rb);
		} catch (MissingResourceException e) {
			// 言語リソースファイルが存在しない
			logger.error("Resource bundle file does not exists.", e);
		}
	}
	
	/**
	 * 引数keyで定義されたメッセージを取得する。
	 * メッセージテンプレート内のプレースホルダー（{0}、{1}、{2}・・・）は、
	 * 2つ目以降の引数のtoString()に置き換えられる。
	 * メッセージが定義されていない場合や、パラメータの置換に失敗した場合はキーが返る。
	 * （ログにエラーが記録される）
	 * 
	 * @param key key メッセージのキー
	 * @param args プレースホルダーを置換する値
	 * @return メッセージ文字列。メッセージが定義されていない場合はキーが返る。
	 */
	public static String get(String key, Object ... args) {
		
		if (bundle == null) {
			return key;
		}
		
		return bundle.format(key, args);
	}

	/**
	 * フレームのタイトル設定時のポイントカット。
	 * @param title
	 */
	@Pointcut("call(void java.awt.Frame+.setTitle(String)) && args(title) " +
			"|| call(void java.awt.Dialog+.setTitle(String)) && args(title)")
	public void setTitle(String title) {}
	
	@Around("setTitle(String) && args(title)")
	public void replaceTitle(String title, ProceedingJoinPoint thisJoinPoint) throws Throwable {
		replaceAndProceed(title, thisJoinPoint);
	}
	
	/**
	 * コンポーネントのラベルやツールチップテキスト設定時のポイントカット。
	 * @param text
	 */
	@Pointcut("(call(void (javax.swing.JLabel+ || java.awt.Label+ || javax.swing.AbstractButton+).setText(String)) " +
			"|| call (void javax.swing.JComponent.setToolTipText(String))) " +
			"&& args(text)")
	public void setText(String text) {}
	
	@Around("setText(String) && args(text)")
	public void replaceText(String text, ProceedingJoinPoint thisJoinPoint) throws Throwable {
		replaceAndProceed(text, thisJoinPoint);
	}
	
	/**
	 * タブのラベル設定時のポイントカット。
	 */
	@Pointcut("call (void javax.swing.JTabbedPane.addTab(..))")
	public void addTab() {}
	
	@Around("addTab()")
	public void replaceTabText(ProceedingJoinPoint thisJoinPoint) throws Throwable {
		Object[] args = thisJoinPoint.getArgs();
		if (bundle.getString((String) args[0]) != null) {
			args[0] = get((String) args[0]);
		}
		thisJoinPoint.proceed(args);
	}
	
	private void replaceAndProceed(String text, ProceedingJoinPoint thisJoinPoint) throws Throwable {
		if (StringUtils.isBlank(text) || bundle.getString(text) == null) {
			thisJoinPoint.proceed();
		} else {
			thisJoinPoint.proceed(new Object[] {get(text)});
		}
	}
	
	/**
	 * 引数keyで定義されたメッセージを取得する。
	 * メッセージテンプレート内のプレースホルダー（{0}、{1}、{2}・・・）は、
	 * 2つ目以降の引数の {@link #get(String, Object...)} の評価値に置き換えられる。
	 * メッセージが定義されていない場合や、パラメータの置換に失敗した場合はキーが返る。
	 * （ログにエラーが記録される）
	 * 
	 * @param key key メッセージのキー
	 * @param args プレースホルダーを置換する値
	 * @return メッセージ文字列。メッセージが定義されていない場合はキーが返る。
	 * @since 1.3.2
	 */
	public static String getWithKeyArgs(String key, String ... keys) {
		
		if (bundle == null) {
			return key;
		}
		
		if (keys == null || keys.length == 0) {
			return get(key);
		}
		
		Object[] args = new Object[keys.length];
		
		for (int i = 0; i < keys.length; i++) {
			args[i] = get(keys[i]);
		}
		
		return bundle.format(key, args);
	}

}
