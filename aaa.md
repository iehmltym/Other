

/**
 * <p>[概 要] コマンド発行クラス。</p>
 * <p>[詳 細] オペレーティングシステムに対してコマンド発行を行う。<br>
 *  　　　　　当クラス内のメソッドに引き渡すコマンド文字列は、予め<br>
 *  　　　　　system.propertiesに定義し、SystemUtil#getSystemInfo<br>
 *  　　　　　と併用して使用することを前提とする。</p>
 * <p>[備 考] </p>
 
public abstract class CallCommand {
    /**
     * ログ。
     */
    private static final Log logger = LogFactory.getLog(CallCommand.class);

    /**
     * バッファサイズ
     */
    private static final int BUFF_SIZE = 256;

    /**
     * <p>
     * [概 要] コンストラクタ。
     * </p>
     * 
     * <p>
     * [詳 細]
     * </p>
     * 
     * <p>
     * [備 考]
     * </p>
     * 
     */
    private CallCommand() {
        //
    }

    /**
     * <p>
     * [概 要] リモートシェル呼出コマンド発行処理。
     * </p>
     *
     * <p>
     * [詳 細] 引数で与えられたリモートシェルコマンド文字列を基にリモートシェルを呼<br>
     * 　　　　び出す。 
     * </p>
     *
     * <p>
     * [備 考] 
     * </p>
     * 
     * @param cmd コマンド名又はシェル名(フルパスを設定)
     * @return コマンド実行終了値(正常終了:0/異常終了：0以外)
     * @throws SystemException システム例外
     */
    public static int callRemoteShell(final String cmd) throws SystemException {
        return callRemoteShell(cmd, null);
    }

    /**
     * <p>
     * [概 要] コマンド引数付きリモートシェル呼出コマンド発行処理。
     * </p>
     *
     * <p>
     * [詳 細] 引数で与えられたリモートシェルコマンド文字列及び、コマンド引数を基に<br>
     * 　　　　リモートシェルを呼び出す。 
     * </p>
     *
     * <p>
     * [備 考] 
     * </p>
     * 
     * @param cmd コマンド名又はシェル名(フルパスを設定)
     * @param params コマンド引数
     * @return コマンド実行終了値(正常終了:0/異常終了：0以外)
     * @throws SystemException システム例外
     */
    public static synchronized int callRemoteShell(final String cmd, final String[] params)
                    throws SystemException {

        final String place = "CallCommand#callRemoteShell()";
        try {
            if (cmd == null || cmd.trim().length() == 0) {
                throw new IllegalArgumentException("Illegal command name. cmd=" + cmd);
            }

            //コマンドライン作成
            StringBuilder sb = new StringBuilder(BUFF_SIZE);
            sb = sb.append("cmd=").append(cmd).append(" ");
            String callCommand = cmd;
            if (params != null) {
                sb = sb.append("params=");
                for (int i = 0; i < params.length; i++) {
                    callCommand = callCommand.replaceAll("%" + (i + 1) + "%", params[i]);
                    sb = sb.append(params[i]);
                    if (i != params.length - 1) {
                        sb = sb.append(",");
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("CallCommand#callRemoteShell() " + sb.toString());
                logger.debug("CallCommand#callRemoteShell() callCommand=" + callCommand);
            }

            //コマンド実行
            //JTEST:JTC-00006:PORT.EXEC-3
            Process process = Runtime.getRuntime().exec(callCommand.trim());
            // ブロック・デッドロック回避
            ProcessWatchThread pw = new ProcessWatchThread(process);
            pw.start();
            final int returnCode = process.waitFor();
            if (logger.isDebugEnabled()) {
                logger.debug("コマンド実行時の戻り値 = " + returnCode);
            }
            pw.setFinished(true);

            return process.exitValue();

        } catch (IllegalArgumentException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e);
            }
            throw new SystemException("E-20024", place, e);
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e);
            }
            throw new SystemException("E-20041", place, e);
        } catch (InterruptedException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e);
            }
            throw new SystemException("E-10099", place, e);
        }
    }

    /**
     * 
     * <p>[概 要] ProcessWatchThreadクラス。</p>
     * <p>[詳 細] リモートシェルより起動されるプロセスを監視する。</p>
     * <p>[備 考] </p>
     * <p>[環 境] JRE5.0</p>
     * <p>Copyright(c) NTT COMWARE 2007</p>
     * @author 覃　宇宝
     */
    private static class ProcessWatchThread extends Thread {
        /**
         * プロセスオブジェクト
         */
        private Process process = null;

        /**
         * プロセス終了フラグ
         */
        private boolean finished = false;

        /**
         * 
         * <p>[概 要] コンストラクタ。</p>
         * <p>[詳 細] このコンストラクタは説明がないので保守できません。</p>
         * <p>[備 考] </p>
         *
         * @param process プロセスオブジェクト
         */
        public ProcessWatchThread(final Process process) {
            this.process = process;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            if (logger.isDebugEnabled()) {
                logger.debug("CallCommand#ProcessWatchThread#run() start.");
            }

            if (process == null) {
                if (logger.isDebugEnabled()) {
                    logger
                                    .debug("CallCommand#ProcessWatchThread#run() end (process null).");
                }
                return;
            }

            final BufferedReader br = new BufferedReader(new InputStreamReader(process
                            .getInputStream()));
            try {
                String buf = null;
                while (true) {
                    do {
                        buf = br.readLine();
                        //donothing
                    } while (buf != null);

                    if (finished) {
                        if (logger.isDebugEnabled()) {
                            logger
                                            .debug("CallCommand#ProcessWatchThread#run() process finished.");
                        }
                        break;
                    }

                    super.yield();
                    if (logger.isDebugEnabled()) {
                        logger.debug("CallCommand#ProcessWatchThread#run() yield.");
                    }
                }
            } catch (final IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("CallCommand#ProcessWatchThread#run()", e);
                }
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("CallCommand#ProcessWatchThread#run()", e);
                    }
                }
            }
        }

        /**
         * 
         * <p>[概 要] setFinishedメソッド。</p>
         * <p>[詳 細] プロセス終了フラグを設定する。</p>
         * <p>[備 考] </p>
         *
         * @param flag フラグ
         */
        public void setFinished(final boolean flag) {
            this.finished = flag;
        }
    }
}


 * <p>[概 要] DBアクセス規定APIインタフェース。</p>
 * <p>[詳 細] DBアクセス規定API用インターフェース。</p>
 * <p>[備 考] 各メソッド内においてSQL文の作成や修正は一切行っていないので、業務AP開発者はSQL文に応じて適切なメソッドをコールすること。<br>
 *            DELETE文が格納された <code>Quary</code> オブジェクトを <code>select(Query)</code> に渡した場合等の動作は未保証。</p>
 
 
 */
public interface DAO {

    /**
     * <p>
     * [概 要] 検索。
     * </p>
     *
     * <p>
     * [詳 細] 引数で引き渡されたクエリを元に、DB検索処理を行う。<br>
     * 　　　　検索結果をObject[]を要素とするListとして呼出元へ返却する。<br>
     * 　　　　Listの各要素が行に、Object[]の各要素が列に対応する。<br>
     *         検索結果が0件の場合、nullではなく要素数0のListを返却する。
     * </p>
     *
     * <p>
     * [備 考] 列の順序は、クエリに記述されているSELECT文どおりに保障される。<br>
     * 　　　　業務APは、当メソッドが返却したListから、Object[]を取出したのち、<br>
     * 　　　　Object[]のひとつひとつに対して、その列が示す属性にキャストする。</p>
     *
     * @param query クエリ
     *
     * @return 検索結果
     *
     * @throws SystemException DB例外
     */
    List select(final Query query) throws SystemException;

    /**
     * <p>
     * [概 要] 検索。
     * </p>
     *
     * <p>
     * [詳 細] 引数で引き渡されたクエリを元に、DB検索処理を行う。<br>
     * 　　　　検索結果をMapを要素とするListとして呼出元へ返却する。<br>
     * 　　　　Listの各要素が行に、Mapの各要素が列に対応する。<br>
     *         検索結果が0件の場合、nullではなく要素数0のListを返却する。
     * </p>
     *
     * <p>
     * [備 考] 列の順序は、クエリに記述されているSELECT文どおりには保障されない。<br>
     * 　　　　業務APは、当メソッドが返却したListから、Mapを取出したのち、更に列<br>
     * 　　　　名をキーにMapからObjectを取出し、取出したObjectに対して、その列が<br>
     * 　　　　示す属性にキャストする。
     * </p>
     *
     * @param query クエリ
     *
     * @return 検索結果
     *
     * @throws SystemException DB例外
     */
    List selectToMap(final Query query) throws SystemException;

    /**
     * <p>
     * [概 要] 挿入。
     * </p>
     *
     * <p>
     * [詳 細] 引数で引き渡されたクエリを元に、DBへ挿入処理を行う。
     * </p>
     *
     * <p>
     * [備 考]
     * </p>
     *
     * @param query クエリ
     *
     * @return 挿入件数
     *
     * @throws SystemException DB例外
     */
    int insert(final Query query) throws SystemException;

    /**
     * <p>
     * [概 要] 更新。
     * </p>
     *
     * <p>
     * [詳 細] 引数で引き渡されたクエリを元に、DBへ更新処理を行う。
     * </p>
     *
     * <p>
     * [備 考]
     * </p>
     *
     * @param query クエリ
     * @return 更新件数
     *
     * @throws SystemException DB例外
     */
    int update(final Query query) throws SystemException;

    /**
     * <p>
     * [概 要] 削除。
     * </p>
     *
     * <p>
     * [詳 細] 引数で引き渡されたクエリを元に、DBへ更新処理を行う。
     * </p>
     *
     * <p>
     * [備 考]
     * </p>
     *
     * @param query クエリ
     *
     * @return 削除件数
     *
     * @throws SystemException DB例外
     */
    int delete(final Query query) throws SystemException;

    /**
     * <p>
     * [概 要] DB切断。
     * </p>
     *
     * <p>
     * [詳 細] DBを切断する。
     * </p>
     *
     * <p>
     * [備 考] 削除予定。
     * </p>
     *
     * @param rs ResultSet
     *
     * @throws SystemException DB例外
     */
    void close(final ResultSet rs) throws SystemException;

    /**
     * <p>
     * [概 要] レコード件数取得。
     * </p>
     *
     * <p>
     * [詳 細] DBのCOUNT()関数を利用して、レコードの存在有無をチェックする。
     * </p>
     *
     * <p>
     * [備 考]
     * </p>
     *
     * @param query クエリ
     *
     * @return 取得出来たレコード件数
     *
     * @throws SystemException DB例外
     */
    int getCount(final Query query) throws SystemException;

    /**
     * <p>
     * [概 要] バッチ更新。
     * </p>
     *
     * <p>
     * [詳 細] 指定されたパラメータの数だけ更新処理をする。
     * </p>
     *
     * <p>
     * * [備 考] 処理結果の更新結果の配列要素には、処理結果件数が必ずしも入るとは<br>
     * とは限らないず「-2」が返される可能性があるので、注意が必要です。<br>
     * java.sql.Statement#executeBatch()の戻り値を参照の事。
     * </p>
     *
     * @param sql クエリ(INSERT, UPDATE, DELETE)
     * @param params パラメータの配列
     *
     * @return 更新結果の配列
     *
     * @throws SystemException DB例外
     *
     * @see java.sql.Statement#addBatch(java.lang.String)
     * @see java.sql.Statement#executeBatch()
     */
    int[] batch(final String sql, final Object[][] params) throws SystemException;

    /**
    * <p>
    * [概 要] カーソルオープン処理。
    * </p>
    *
    * <p>
    * [詳 細] フェッチ用のカーソルをオープンする。<br>
    * 　　　　引数に引き渡されたクエリを元に、DB検索処理を行いカーソルを保持する。<br>
    * 　　　　オープンしたカーソルは使用後に必ずクローズメソッドをCallすること。
    * </p>
    *
    * <p>
    * [備 考] 当メソッド使用時には、「Select ～ For Update」は使用しないこと。<br>
    *         （検索対象データが全て排他制御対象となってしまうため)
    * </p>
    *
    * @param query クエリ
    *
    * @throws SystemException DB例外
    *
    * @see #fetch()
    * @see #closeFetchCursor()
    */
   void openFetchCursor(final Query query) throws SystemException;


   /**
    * <p>
    * [概 要] フェッチ処理。
    * </p>
    *
    * <p>
    * [詳 細] カーソルを順方向へ移動する。
    * </p>
    *
    * <p>
    * [備 考] 事前条件:カーソルをオープンしておくこと。
    * 　　　　（次行が存在しない場合、空のListを返す。）
    * </p>
    *
    * @return 取得結果（1レコード分）
    * @throws SystemException DB例外
    *
    * @see #openFetchCursor(Query)
    */
   List fetch() throws SystemException;


   /**
    * <p>
    * [概 要] カーソルクローズ処理。
    * </p>
    *
    * <p>
    * [詳 細] カーソルをクローズする。
    * </p>
    *
    * <p>
    * [備 考] オープンしたカーソルを使用後に、当メソッドを使用して必ずクローズすること。
    * </p>
    *
    * @throws SystemException DB例外
    *
    * @see #openFetchCursor(Query)
    */
   void closeFetchCursor() throws SystemException;
   
   /**
    * 
    * <p>
    * [概 要] creatCallableStatementメソッド。
    * </p>
    * 
    * <p>
    * [詳 細] このメソッドの詳細を記述する。 
    * </p>
    *     必要であれば、ここに table 要素などを記述できる。
    * <p>
    * [備 考] 自由に書く。 
    * </p>
    *
    * @param query クエリ
    * @return CallableStatement
    * @throws SystemException SQL例外
    */
   CallableStatement creatCallableStatement(final Query query) throws SystemException;
}


