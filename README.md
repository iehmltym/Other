import "../../../common/pkgBasicParts.crs";
import "../../../common/pkgKakuchoParts.crs";
import "../../../common/pkgHttp.crs";
import "../bizconf/pkg316020Conf.crs";
import "../bizcommon/pkg316020Common.crs";

clsBaseDialog dlgG316020040_07 {
	
	Record kensakuJouken{
		String setubi = "";		/* 設備 */
		String syumoku = "";		/* 種目 */
		String saimoku = "";		/* 細目 */
		String saibun = "";	/* 細分 */
		String seiri_cd = "";		/* 整理コード */
	}
	
		
	/* @[概要]:本体内訳名の検索; */
	Function commonRequest(){
		try{
			dlgG316020040_07.dialogFrame.btnOK.Active = $TRUE;
			dlgG316020040_07.dialogFrame.txfTekiokubun.Value = pkg316020Common.yuguzeisei.tekiokubn;
			dlgG316020040_07.dialogFrame.lblTekiokubun_nm1.Value = pkg316020Common.yuguzeisei.tekiokubnnm;
			dlgG316020040_07.dialogFrame.txfSetubisaibun_nm.Value = pkg316020Common.yuguzeisei.setubisaibunnm;
			dlgG316020040_07.dialogFrame.lblTekionendo1.Value
			= pkg316020Common.yuguzeisei.tekiyonendo + "年度（" + pkg316020Common.yuguzeisei.kikankaisi 
			+ "～" + pkg316020Common.yuguzeisei.kikanryo + "）";
	}
		 catch( e ){
			pkgMessage.putException(e,this.name);
			pkgScreenControl.lockScreen (frmG316020040);
		}
	}


	clsSekoDialog dialogFrame {
		Width = 608;
		Height = 197;

		/* 画面タイトル */
		lblTitle.Value = "優遇税制参照";
		
		
		
		/* ボタン; */
		clsKinouButton btnOK {
			X = 10;
			Y = ^.Height - 26;
			Title = "Ｏ　Ｋ";
			
			Function OnTouch( e ) {
				
				try {
					/* ダイアログ消去 */
					^.^.Delete();
					
				} catch (e) {
					pkgMessage.putException(e, this.Name);
				}
			}
		}
		
		clsBaseLabel lblTekiokubun {
			X = 38;
			Y = 48;
			Width = 70;
			Height = 20;
			Value = "適応区分: ";
		}
		clsBaseTextFrame txfTekiokubun {
			X = 108;
			Y = 48;
			Width = 490;
			Height = 30;
			Value = "ＮＮＮＮＮＮＮＮＮ１ＮＮＮＮＮＮＮＮＮ２ＮＮＮＮＮＮＮＮＮ３ＮＮＮＮＮＮＮＮＮ４ＮＮＮＮＮＮＮＮＮ５ＮＮＮＮＮＮＮＮＮ６ＮＮＮＮＮＮＮＮＮ７";
			VerticalAlign = $CENTER;
			HorizontalAlign = $LEFT;
			Border = $FALSE;
			UseChange = $TRUE;
			ScrollBarPosition = $NONE;
			Active = $FALSE;
			BgColor = pkgPartsGlobal.HMI_BGCOLOR_READ;
		}
		clsBaseLabel lblTekiokubun_nm {
			X = 10;
			Y = 81;
			Width = 98;
			Height = 20;
			Value = "適応区分名称: ";
		}
		clsBaseLabel lblTekiokubun_nm1 {
			X = 108;
			Y = 81;
			Width = 490;
			Height = 20;
			HorizontalAlign = $LEFT;
			Value = "ＮＮＮＮＮＮＮＮＮ１ＮＮＮＮＮＮＮＮＮ２ＮＮＮＮＮＮＮＮＮ３ＮＮＮ３５";
		}
		clsBaseLabel lblSetubisaibun_nm {
			X = 24;
			Y = 104;
			Width = 84;
			Height = 20;
			Value = "設備細分名: ";
		}
		clsBaseTextFrame txfSetubisaibun_nm {
			X = 108;
			Y = 104;
			Width = 420;
			Height = 30;
			Border = $FALSE;
			HorizontalAlign = $LEFT;
			VerticalAlign = $CENTER;
			UseChange = $TRUE;
			Value = "ＮＮＮＮＮＮＮＮＮ１ＮＮＮＮＮＮＮＮＮ２ＮＮＮＮＮＮＮＮＮ３ＮＮＮＮＮＮＮＮＮ４ＮＮＮＮＮＮＮＮＮ５ＮＮＮＮＮＮＮＮＮ６";
			ScrollBarPosition = $NONE;
			BgColor = pkgPartsGlobal.HMI_BGCOLOR_READ;
			Active = $FALSE;
		}
		clsBaseLabel lblTekionendo {
			X = 38;
			Y = 137;
			Width = 70;
			Height = 20;
			Value = "適応年度: ";
		}
		clsBaseLabel lblTekionendo1 {
			X = 108;
			Y = 137;
			Width = 364;
			Height = 20;
			HorizontalAlign = $LEFT;
			Value = "ＹＹＹＹ年度(ＹＹＹＹ-ＭＭ-ＤＤ～ＹＹＹＹ-ＭＭ-ＤＤ)";
		}
		
		if ( !$DESIGNTIME ) {
			setWindowTitle("1.0");
		}
		
	}
}
	
	if ( !$DESIGNTIME ) {
		/* OKボタンをDisable */
		dlgG316020040_07.dialogFrame.btnOK.Active = $FALSE;
		
		/* サーバ処理呼出し */
		dlgG316020040_07.commonRequest();
	}
