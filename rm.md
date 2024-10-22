import React, { useEffect, useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { BaseDialogProps } from '../../../common/PkgType';
import PkgMessage from '../../../common/PkgMessage';
import PkgScreenControl from '../../../common/PkgScreenControl';
import PkgHttp from '../../../common/PkgHttp';

// 検索結果のインターフェース
interface SearchResult {
  tekiokubn: string;      // 適応区分
  tekiokubnnm: string;    // 適応区分名称
  setubisaibunnm: string; // 設備細分名
  tekiyonendo: string;    // 適応年度
  kikankaisi: string;     // 期間開始
  kikanryo: string;       // 期間終了
}

interface DlgG316020040_07Props extends BaseDialogProps {
  onConfirm?: () => void;
  kensakuJouken?: {
    setubi: string;      // 設備
    syumoku: string;     // 種目
    saimoku: string;     // 細目
    saibun: string;      // 細分
    seiri_cd: string;    // 整理コード
  };
}

const DlgG316020040_07 = ({ 
  show = false, 
  setShow,
  onConfirm,
  kensakuJouken 
}: DlgG316020040_07Props) => {
  // 検索結果のstate
  const [searchData, setSearchData] = useState<SearchResult | null>(null);
  // ローディング状態
  const [isLoading, setIsLoading] = useState<boolean>(false);

  // 本体内訳名の検索処理
  const commonRequest = async () => {
    try {
      setIsLoading(true);
      
      // 検索パラメータの設定
      const params = {
        SETUBI: kensakuJouken?.setubi || "",
        SYUMOKU: kensakuJouken?.syumoku || "",
        SAIMOKU: kensakuJouken?.saimoku || "",
        SAIBUN: kensakuJouken?.saibun || "",
        SEIRI_CD: kensakuJouken?.seiri_cd || ""
      };

      // サーバー呼び出し
      const response = await PkgHttp.callGateway<any>("316020040", params);

      if (response.RESULT === PkgHttp.Result.SUCCESS) {
        setSearchData({
          tekiokubn: response.BUSINESS_DATA.TEKIOKUBN,
          tekiokubnnm: response.BUSINESS_DATA.TEKIOKUBNNM,
          setubisaibunnm: response.BUSINESS_DATA.SETUBISAIBUNNM,
          tekiyonendo: response.BUSINESS_DATA.TEKIYONENDO,
          kikankaisi: response.BUSINESS_DATA.KIKANKAISI,
          kikanryo: response.BUSINESS_DATA.KIKANRYO
        });
      } else if (response.RESULT === PkgHttp.Result.BUSINESS_ERROR) {
        PkgMessage.putMessageBox(response.MESSAGE_ID, [response.MESSAGE]);
        setShow(false);
      } else {
        PkgScreenControl.lockDialog('DlgG316020040_07');
        setShow(false);
      }
    } catch (error) {
      PkgMessage.putException(error, 'commonRequest');
      PkgScreenControl.lockDialog('DlgG316020040_07');
    } finally {
      setIsLoading(false);
    }
  };

  // ダイアログ表示時に検索実行
  useEffect(() => {
    if (show) {
      commonRequest();
    }
  }, [show]);

  // OK button handler
  const handleConfirm = () => {
    try {
      onConfirm?.();
      setShow(false);
    } catch (error) {
      PkgMessage.putException(error, 'handleConfirm');
      PkgScreenControl.lockDialog('DlgG316020040_07');
    }
  };

  return (
    <Dialog open={show} onOpenChange={setShow}>
      <DialogContent className="sm:max-w-[608px] bg-[#F0F0F0]"> {/* 背景色を図2に合わせる */}
        <DialogHeader>
          <DialogTitle className="text-base font-normal">優遇税制参照</DialogTitle>
        </DialogHeader>

        {isLoading ? (
          <div className="flex justify-center py-4">読み込み中...</div>
        ) : (
          <div className="space-y-4 py-4">
            {/* 適応区分 */}
            <div className="flex items-start space-x-4">
              <span className="w-24 text-right pt-2">適応区分:</span>
              <div className="flex-1 p-2 bg-white border min-h-[30px]"> {/* 白背景と境界線を追加 */}
                {searchData?.tekiokubn || ''}
              </div>
            </div>

            {/* 適応区分名称 */}
            <div className="flex items-start space-x-4">
              <span className="w-24 text-right pt-2">適応区分名称:</span>
              <div className="flex-1 py-2">
                {searchData?.tekiokubnnm || ''}
              </div>
            </div>

            {/* 設備細分名 */}
            <div className="flex items-start space-x-4">
              <span className="w-24 text-right pt-2">設備細分名:</span>
              <div className="flex-1 p-2 bg-white border min-h-[30px]">
                {searchData?.setubisaibunnm || ''}
              </div>
            </div>

            {/* 適応年度 */}
            <div className="flex items-start space-x-4">
              <span className="w-24 text-right pt-2">適応年度:</span>
              <div className="flex-1 py-2">
                {searchData?.tekiyonendo && 
                  `${searchData.tekiyonendo}年度（${searchData.kikankaisi}～${searchData.kikanryo}）`
                }
              </div>
            </div>
          </div>
        )}

        <div className="flex justify-start mt-4">
          <button
            onClick={handleConfirm}
            className="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
            disabled={isLoading}
          >
            Ｏ　Ｋ
          </button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default DlgG316020040_07;
