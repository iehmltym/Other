import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import PkgBasicParts from '../../../common/PkgBasicParts';
import PkgHttp from '../../../common/PkgHttp';
import PkgMessage from '../../../common/PkgMessage';
import PkgScreenControl from '../../../common/PkgScreenControl';
import { BaseDialogProps, ButtonOnClickEvent } from '../../../common/PkgType';

const DlgG316020040_07 = {
  Dialog: ({ props }: DlgG316020040_07Props) => {
    /** 画面ID */
    const GID = "G316020040_07";
    /** 画面Version */
    const GVER = "1.0";

    // モックアップデータ
    const mockData = {
      tekiokubn: "優遇税制適応区分テスト",
      tekiokubnnm: "区分名称テスト",
      setubisaibunnm: "対象設備名称テスト",
      tekiyonendo: "2024",
      kikankaisi: "2024-04-01",
      kikanryo: "2025-03-31"
    };

    /**
     * OKボタン押下時の処理
     */
    const handleOkClick = async (ev: ButtonOnClickEvent) => {
      try {
        // ダイアログを閉じる
        props.setShow(false);
        if (props.btnOkClickEvent) {
          props.btnOkClickEvent(ev);
        }
      } catch (e) {
        PkgMessage.putException(e, "handleOkClick");
        PkgScreenControl.lockDialog(GID);
      }
    };

    // フッターボタン定義
    const footerButtons = [
      { 
        id: "btnOkG316020040_07", 
        text: "Ｏ　Ｋ", 
        onClick: handleOkClick,
      }
    ];

    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
        <Card className="w-[608px]">
          <CardHeader>
            <CardTitle>優遇税制参照</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="space-y-4">
              <div className="flex items-center">
                <span className="w-24 text-right mr-2">適応区分:</span>
                <div className="flex-1 p-2 bg-gray-100 min-h-[30px]">
                  {mockData.tekiokubn}
                </div>
              </div>

              <div className="flex items-center">
                <span className="w-24 text-right mr-2">適応区分名称:</span>
                <div className="flex-1">
                  {mockData.tekiokubnnm}
                </div>
              </div>

              <div className="flex items-center">
                <span className="w-24 text-right mr-2">設備細分名:</span>
                <div className="flex-1 p-2 bg-gray-100 min-h-[30px]">
                  {mockData.setubisaibunnm}
                </div>
              </div>

              <div className="flex items-center">
                <span className="w-24 text-right mr-2">適応年度:</span>
                <div className="flex-1">
                  {`${mockData.tekiyonendo}年度（${mockData.kikankaisi}～${mockData.kikanryo}）`}
                </div>
              </div>
            </div>

            <div className="flex justify-start mt-4">
              <button
                className="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                onClick={handleOkClick}
              >
                Ｏ　Ｋ
              </button>
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }
};

export default DlgG316020040_07;

export type DlgG316020040_07Props = {
  props: BaseDialogProps & {
    btnOkClickEvent?: ButtonOnClickEvent;
  };
};
