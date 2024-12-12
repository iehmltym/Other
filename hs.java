package mytest0904;

import java.security.MessageDigest; // メッセージダイジェストを使用するためのインポート
import java.nio.charset.StandardCharsets; // 文字エンコーディングを扱うためのインポート

public class PasswordHashWithSalt {
    // ハッシュ生成時に使用する固定文字列（ソルトに追加される）
    private static final String FIXED_STRING = "smartnul_pass#00";

    private static final String[][] TEST_DATA = {
        {"abcdefghi", "-1211320280.770840591939146"},
        {"ABCDEFGHI", "-1221327880.924923207584018"},
        {"123456789", "-1199194280.0435162265372445"},
        {"---___@@@", "-1194910380.975468037146511"},
        {"abcdEFGHI", "-1205042680.168292572875466"},
        {"abcdEFG123", "-1214965180.632799775176595"},
        {"abCD12-@", "-1224734780.3846028362542"},
        {"abcdefghiJKLMNOPQR123456789-_@", "-1200602780.954978585024355"}
    };

    public static String generateHashWithSalt(String password, String salt) {
        try {
            // パスワード、固定文字列、ソルトを結合してハッシュ化する文字列を作成
            String stringToHash = password + FIXED_STRING + salt;

            // SHA-1メッセージダイジェストインスタンスを取得
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // 文字列をUTF-8でバイト配列に変換し、ハッシュ化
            byte[] hash = md.digest(stringToHash.getBytes(StandardCharsets.UTF_8));

            // バイト配列を16進数文字列に変換
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                // バイトを16進数に変換（常に2桁表記）
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');  // 1桁の場合は先頭に0を追加
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            // ハッシュ生成中の例外をラップ
            throw new RuntimeException("ハッシュ生成に失敗しました", e);
        }
    }

    /**
     * メインメソッド：テストデータを使用してハッシュ生成を検証する
     */
    public static void main(String[] args) {
        System.out.println("パスワードハッシュ生成検証：");
        System.out.println("----------------------------------------");


        // 各テストデータに対してハッシュを生成し、詳細を表示
        for (String[] data : TEST_DATA) {
            String password = data[0];  // パスワード
            String salt = data[1];      // ソルト値

            // ハッシュ生成
            String generatedHash = generateHashWithSalt(password, salt);

            // 各テストケースの詳細を出力
            System.out.println("パスワード: " + password);
            System.out.println("ソルト値: " + salt);
            System.out.println("生成されたハッシュ: " + generatedHash);
            System.out.println("----------------------------------------");
        }

    }
}
