/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.client.ui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import game.vo.Data;
import game.vo.Friend;
import game.vo.GameInfo;
import game.vo.GameRoom;
import game.vo.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.*;

/**
 *
 * @author user
 */
public class FXMLController implements Runnable, Initializable {

	ArrayList<String> makerID = new ArrayList<>();
	ArrayList<String> roomtitlearr = new ArrayList<>();
	

	// 접속단계
	@FXML
	private ListView<Object> allUserList;
	@FXML
	private AnchorPane loadingPane;
	@FXML
	private ProgressIndicator ProgressIndicator;

	// 로그인
	@FXML
	private Button loginList;
	@FXML
	private TextField field_id;
	@FXML
	private TextField field_pw;
	@FXML
	private Button btnLogin;
	@FXML
	private HBox loginBox;
	@FXML
	private ListView<Object> friendList;
	@FXML
	private StackPane loginInfoPane;
	@FXML
	private Text welcomeTxt;
	@FXML
	private AnchorPane CommandPane;
	@FXML
	private Group chatGroup;

	// 로그아웃
	@FXML
	private Button btnLogOut;

	// 회원가입
	@FXML
	private CheckBox checkAgree;
	@FXML
	private Button btnSignUp;
	@FXML
	private Button btnOK;
	@FXML
	private Button btnCancel;
	@FXML
	private AnchorPane AnchorSignUp;// 회원가입 패널
	@FXML
	private TextField signID;
	@FXML
	private TextField signPW;
	@FXML
	private TextField confirmPW;
	@FXML
	private TextField signMail;

	@FXML
	private ToolBar toolbar;
	@FXML
	private Button btnSplit;
	@FXML
	private SplitPane splitPane;
	@FXML
	private Button btnFold;
	@FXML
	private Button btnSpread;

	// 채팅(방 들어가기 전 / 들어간 후 구별해서)
	@FXML
	private TextArea txtArea_chatLog;
	@FXML
	private TextField field_chat;
	@FXML
	private Button btn_send;

	// 메인화면(홈)
	@FXML
	private Button btnMain;
	@FXML
	private Group groupBtnRoom;
	// 게임선택
	@FXML
	private AnchorPane mainPane;
	@FXML
	private ImageView main01;
	@FXML
	private ImageView main02;
	@FXML
	private TextArea txtArea_main01;
	@FXML
	private TextArea txtArea_main02;
	@FXML
	private TextArea txtArea_main03;
	@FXML
	private ScrollPane roomListPane;

	// 방만들기
	@FXML
	private Button btnCreateRoom;
	@FXML
	private Button btnMkRoom;
	@FXML
	private Button btnMkCancel;
	@FXML
	private Button btnCreateCancel;
	@FXML
	private TextField field_RoomTitle;
	@FXML
	private TextField field_RoomPW;
	@FXML
	private AnchorPane RoomPane;

	// 게임 선택 후 게임방목록 테이블
	@FXML
	private TableView tableView;
	@FXML
	private TableColumn column01;
	@FXML
	private TableColumn column02;
	@FXML
	private TableColumn column03;

	private final ObservableList<modelClass> tdata = FXCollections.observableArrayList();

	// 게임방
	@FXML
	private Canvas canvas;
	@FXML
	private Button clearCanvas;
	@FXML
	private Region canvasReign;
	@FXML
	private AnchorPane gamePane;
	@FXML
	private Slider slider;
	@FXML
	private ColorPicker colorPicker;
	@FXML
	private Button btnOut;
	@FXML
	private Text TxtAnswer; // 제시어

	private GraphicsContext gc;
	private int i;

	// 스레드 실행 위함
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	static ArrayList<User> connectedUserList = new ArrayList<>(); // 서버에 접속된
	// 클라이언트, 각 // 클라이언트의 // ObjectOutputStream이 // 저정되어 있음
	static HashMap<String, GameRoom> gameRoomList = new HashMap<>();
	private Data data;
	private User user;
	private GameRoom ri;
	private String roomTitle;
	private boolean exit;
	private FXMLController fxControl;// FX의 컴포넌트 갱신 위해 스레드 실행 시 받아옴
	private User loginUser;// 나나나 미미미

	private GameInfo ginfo;

	public FXMLController() {//

		try {
			// 서버연결
			socket = new Socket("localhost", 7582);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());

			// 접속후 서버에서 뿌려주도록 스레드 실행 (로그인 안해도)
			// ClientListenerThread ct = new ClientListenerThread(socket, this);
			Thread t = new Thread(this);
			t.start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 회원가입 과정에 필요한 이벤트
	 */
	@FXML
	private void signUpAction(ActionEvent e) throws IOException {
		System.out.println("회원가입 액션 메소드실행");

		splitPane.setDividerPosition(0, 0);// 전체화면에서는 최대한 펼쳐짐
		AnchorSignUp.setVisible(true);

		// 새창으로 열어서 할 경우
		// Stage stage;
		// Parent root;
		// this.PaneLoading(true);
		// loadingPane.setVisible(true);
		// stage = new Stage();
		// root = FXMLLoader.load(getClass().getResource("./Login.fxml"));
		// stage.setScene(new Scene(root));
		// stage.setTitle("로그인창");
		// stage.initModality(Modality.APPLICATION_MODAL);
		// stage.initStyle(StageStyle.TRANSPARENT);// 스테이지 모양
		// stage.initOwner(btnSignUp.getScene().getWindow());
		// stage.showAndWait();
		// 확인
		if (e.getSource() == btnOK) {
			if (checkAgree.isSelected() == false) {
				JOptionPane.showMessageDialog(null, "약관동의 체크를 해주세요");
			} else {

				String id = signID.getText();
				String pw = signPW.getText();
				String pw2 = confirmPW.getText();
				String mail = signMail.getText();

				boolean check1 = id.isEmpty();
				boolean check2 = pw.isEmpty();
				boolean check3 = pw2.isEmpty();
				boolean check4 = mail.isEmpty();

				if (check1 == true && check2 == true && check3 == true && check4 == true) {
					JOptionPane.showMessageDialog(null, "빈칸을 모두 입력해주세요");
				} else if (pw.equals(pw2) != true) {
					JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다.");// FIXME
					// 리스너에서
					signPW.setText("");
					confirmPW.setText("");
				} else {
					User new_user = new User(id, pw, mail, User.IMAGE1);// 아직
					// 그림
					// 없음
					data.setUser(new_user);
					data.setCommand(Data.SIGNUP);
					this.sendData(data);
					btnCancel.getScene().getWindow();
					AnchorSignUp.setVisible(false);

				} // inner if // outer if
			} // checkAgree 분기
		} // btnOK

		// 닫기
		if (e.getSource() == btnCancel) {
			btnCancel.getScene().getWindow();
			AnchorSignUp.setVisible(false);
		}
	}

	/**
	 * 자잘한 버튼 이벤트
	 * 
	 * @param e
	 * @throws IOException
	 */
	@FXML
	private void ButtonAction(ActionEvent e) throws IOException {
		boolean toggle = true;
		if (e.getSource() == btnSplit) {

			if (toggle) {
				toggle = false;
			} else {
				toggle = true;
			}
		} else if (e.getSource() == btnFold) {
			splitPane.setDividerPosition(0, 1);
			btnFold.setVisible(false);
			btnSpread.setVisible(true);
			canvas.autosize();
		} else if (e.getSource() == btnSpread) {
			splitPane.setDividerPosition(0, 0);
			btnFold.setVisible(true);
			btnSpread.setVisible(false);
			// canvas.setWidth(1000);
			// canvas.setHeight(canvasReign.getHeight());
			canvas.autosize();
			startDraw(gc);

		} else if (e.getSource() == btnMain) {// 메인화면
			mainPane.setVisible(true);
		}

	}

	/**
	 * 로그인 과정에 필요한 이벤트
	 */
	@FXML
	private void loginAction(ActionEvent e) throws IOException {
		if (e.getSource() == btnLogin) {
			String id = field_id.getText();
			String pw = field_pw.getText();
			if (id.equals("") || pw.equals("")) {
				JOptionPane.showMessageDialog(null, "아이디와 패스워드를 입력해주세요");
			} else {

				loginUser = new User(id, pw, "", 0);
				Data data = new Data(Data.LOGIN);
				data.setUser(loginUser);
				this.sendData(data);
				System.out.println("1. 로그인명령 전송완료");

			}
		}
	}

	/**
	 * 닫기에 필요한 이벤트
	 */
	@FXML
	private void PaneLoading(boolean toggle) throws IOException {
		if (toggle) {
			// ProgressIndicator.setVisible(toggle);
			loadingPane.setVisible(true);
			loadingPane.setOpacity(1.d);
			for (int i = 0; i < 10; i++) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			loadingPane.setOpacity(0);
			loadingPane.setVisible(false);
		}
	}

	/**
	 * 외부 프로그램 실행
	 * 
	 * @param e
	 */
	@FXML
	public void exeCuteGameAction(MouseEvent e) {
		loadingPane.setVisible(true);
		// TODO 외부파일 실행 스레드 생성
		JOptionPane.showMessageDialog(null, "게임을 실행 중 입니다. 잠시만 기다려주세요");
		txtArea_main01.setDisable(false);// 선택한 게임의 설명 텍스트에리어 활성화
		txtArea_main02.setDisable(true);
		txtArea_main03.setDisable(true);

		try {
			Runtime.getRuntime().exec("UnityShooting/");// FIXME 실행파일 경로 수정
		} catch (IOException e2) {
			e2.printStackTrace();
			JOptionPane.showMessageDialog(null, "파일을 찾을 수 없습니다.");
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		loadingPane.setVisible(false);

	}

	/**
	 * 캐치마인드 실행
	 * 
	 * @param e
	 */
	@FXML
	public void catchmindExeAction(MouseEvent e) {
		// TODO 테이블에 방 들어감

		JOptionPane.showMessageDialog(null, "아재마인드 게임을 선택하셨습니다. " + "\n" + "방만들기를 하시거나 우측테이블에서 방을 클릭해주세요");

		groupBtnRoom.setDisable(false);
		data.setUser(loginUser);
		data.getUser().setSelectedGame(Data.GAME_SECOND);// 내 정보에도 저장

		data.setCommand(Data.SELECT_GAME);
		data.setGameType(Data.GAME_SECOND);
		this.sendData(data);
		txtArea_main01.setDisable(true);// 선택한 게임의 설명 텍스트에리어 활성화
		txtArea_main02.setDisable(false);
		txtArea_main03.setDisable(true);
	}

	public void saakmindExeAction(MouseEvent e) {
		JOptionPane.showMessageDialog(null, "사악마인드 게임을 선택하셨습니다. " + "\n" + "방만들기를 하시거나 우측테이블에서 방을 클릭해주세요");
		groupBtnRoom.setDisable(false);
		groupBtnRoom.setDisable(false);
		data.setCommand(Data.SELECT_GAME);
		data.setGameType(Data.GAME_THIRD);
		this.sendData(data);
		txtArea_main01.setDisable(true);// 선택한 게임의 설명 텍스트에리어 활성화
		txtArea_main02.setDisable(true);
		txtArea_main03.setDisable(false);
	}

	/**
	 * 그림 지우기 버튼 메소드
	 * 
	 * @param e
	 */
	@FXML
	private void clearAction(ActionEvent e) {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);// 선 굵기
		gc.strokeRect(0, // x of the upper left corner
				0, // y of the upper left corner
				1000, // width of the rectangle
				gamePane.getHeight()); // height of the rectangle

		data.setCommand(Data.CLEAR_CANVAS);// TODO 유저정보를 안넣어주었는걸? 왜냐면 귀찮아서 ..
											// 안넣어도 잘 되었으면...
		sendData(data);
	}

	private void clearActionOrder() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);// 선 굵기
		gc.strokeRect(0, // x of the upper left corner
				0, // y of the upper left corner
				1000, // width of the rectangle
				gamePane.getHeight()); // height of the rectangle

	}

	/**
	 * 방만드는데 필요한 버튼메소드
	 * 
	 * @param e
	 */
	@FXML
	public void mkRoomAction(ActionEvent e) {
		mainPane.setVisible(false);
		Object s = e.getSource();
		if (s == btnCreateRoom) {// 방만들기창 열기
			RoomPane.setVisible(true);

		} else if (s == btnCreateCancel) {// 방만들기 취소
			RoomPane.setVisible(false);
			field_RoomTitle.setText("");
			field_RoomPW.setText("");
		} else if (s == btnMkRoom) {// 방생성

			String roomTitle = field_RoomTitle.getText();
			String roomPw = field_RoomPW.getText();
			if (roomTitle.equals("") != true) {
				GameRoom room = new GameRoom(loginUser, roomTitle, roomPw);
				data.setCommand(Data.MAKE_ROOM);
				data.setGameRoom(room);

				this.sendData(data);
				
				this.showGamePane();
				
			} else
				JOptionPane.showMessageDialog(null, "정보를 입력해주세요");
		} else if (s == btnMkCancel) {// 방만들기 취소
			RoomPane.setVisible(false);
			field_RoomTitle.setText("");
			field_RoomPW.setText("");
		}

		else if (s == btnOut) {// 방나가기 >> 홈이 아닌 메인으로 가게함 , 즉 GUI 초기화 역할
			// TODO GUI제어만 할게 아니라 게임 중이었으면 내보내서 브로드캐스팅 안받게 해주어야함
			groupBtnRoom.setDisable(true);// 방만들기 버튼그룹 닫기

			btnCreateRoom.setDisable(false);
			btnCreateCancel.setDisable(false);
			gamePane.setVisible(false);
			this.makeNewCanvas();
			field_RoomTitle.setText("");
			field_RoomPW.setText("");

			txtArea_main01.setDisable(true);
			txtArea_main02.setDisable(true);
			txtArea_main03.setDisable(true);
			data.setGameType(0);// 디폴트로 바꿔줌
			
			tdata.clear();
		}
	}

	private void showGamePane() {
		RoomPane.setVisible(false);
		gamePane.setVisible(true);

		btnCreateRoom.setDisable(true);
		btnCreateCancel.setDisable(true);
		
		splitPane.setDividerPosition(0, 0);
	}

	private void makeNewCanvas() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);// 선 굵기
		gc.strokeRect(0, // x of the upper left corner
				0, // y of the upper left corner
				1000, // width of the rectangle
				gamePane.getHeight()); // height of the rectangle

	}

	/**
	 * 그리는 펜 굵기 바꿔줌
	 * 
	 * @param e
	 */
	@FXML
	private void setDrawLine(DragEvent e) {
		double val = slider.getValue();// 슬라이더 선택한 숫자
		System.out.println(val);

	}

	/**
	 * 소켓 연결 후 실행되는 리스너스레드, 변화를 반영
	 */
	@FXML
	@Override
	public void run() {

		System.out.println("리스너 스레드 접속");
		// 접속하자마자 접속자 리스트 불러올 command 보냄 (Command만 들어있음)
		// data.setCommand(Data.CONNECTION);
		data = new Data(Data.CONNECTION);// 데이터 객체 최초 생성
		this.sendData(data);
		System.out.println("CONNECTION명령보냄");
		while (!exit) {
			try {
				data = (Data) ois.readObject();
				switch (data.getCommand()) {
				case Data.CONNECTION: // 전체 접속자 정보 받아와서 ui에 띄워주기
					connectedUserList = data.getUserList();
					// TODO:userList 갱신 메소드 만들기

					// loadingPane.setVisible(false);
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							if (connectedUserList.isEmpty() == false) {
								System.out.println("입력스레드시작");
								renewalConUserList();
							} else {

							}
							loadingPane.setVisible(false);
						}

					});
					System.out.println("CONNECTION명령 리슨 완료");
					break;
				case Data.SIGNUP:
					System.out.println("SIGNUP 명령 받음");
					/**
					 * 회원가입 버튼 눌렀을 때 UI 액션리스너에서 서버에 oos로 넘길 내용 TODO: id, pw, em,
					 * pfimg 선택(랜덤), 받아서 User 객체 만들기 하나라도 입력 안하면 오류남! 에러 처리 해줄것!
					 * 서버에 user Data.SIGNIN setCommand 해서 넘겨주기 TODO: 서버에서
					 * boolean(database 등록되었는지 안되었는지 받음) 결과에 따라
					 */
					boolean addrs = data.isUserAddrs();
					if (addrs) { // TODO:등록되었을 경우 - 메세지창:"등록 완료되었습니다."
						JOptionPane.showMessageDialog(null, "가입되셨습니다. 로그인해주세요");
					} else {
						// TODO:등록되지 않았을 경우 - 메세지창: "이미 등록된 아이디입니다. 다른 아이디를 입력해
						// 주세요"
						// 회원가입 textfield 아이디 창만 리셋해주기
						JOptionPane.showMessageDialog(null, "이미 등록된 아이디입니다. ");
					}
					System.out.println("SIGNUP 처리완료");
					break;
				case Data.LOGIN:
					/**
					 * 로그인 버튼
					 *
					 */
					System.out.println("2. 로그인명령 처리결과 들어옴");

					User user = data.getUser();// 로그인한사람
					if (user == null) {// 비번이 틀렸거나 이미 로그인 중일 때
						String errorMsg = data.getError();
						if (errorMsg.equals("이미 로그인 중")) {
							JOptionPane.showMessageDialog(null, "이미 로그인 중인 아이디 입니다.");
						} else
							JOptionPane.showMessageDialog(null, "비밀번호가 틀렸습니다.");

					} else {// 정상로그인
						System.out.println(user + "로그인명령에서 받아온 유저데이터");
						connectedUserList = data.getUserList();
						System.out.println(connectedUserList + "로그인명령에서 받아온 유저리스트");
						ArrayList<Friend> friendList = data.getFriendList();
						// renewalConUserList();
						System.out.println("3. 로그인명령 처리 결과 UI반영");
						// GUI 로그인 안보이도록 함

						loginBox.setVisible(false);// 로그인 정보 입력 패널 숨기기
						loginInfoPane.setVisible(true);
						welcomeTxt.setText("아이디 : " + loginUser.getId());
						// FIXME 0으로 임시 변경
						this.showloadingPane(0);// 스트림 보내고 리스너 기다리는 동안
						JOptionPane.showMessageDialog(null, loginUser.getId() + "님 접속을 환영합니다.");

						// GUI활성화
						CommandPane.setDisable(false);
						txtArea_chatLog.setDisable(false);
						chatGroup.setDisable(false);
						roomListPane.setDisable(false);
						mainPane.setDisable(false);
						// mainPane.setVisible(true);//바로 홈 보이기, 메인버튼
					}
					break;
				case Data.GUL:
					connectedUserList = data.getUserList();
					// 접속자갱신
					renewalConUserList();
					break;
				case Data.SELECT_GAME:
					roomList = data.getRoomList();
					this.renewAllTable(roomList);

					break;
				case Data.JOIN:
					data.getUser();//방장분기조건 
					data.getGameType();//게임타입분기조건
					boolean check1 = this.myjoinRoomID.equals(data.getJoinRoomID());//조인한 사람만(방장제외 들어온 사람)
					boolean check2 = this.loginUser.equals(data.getJoinRoomID());//내가 만든 방인 경우 
					if (check1||check2) {
						//TODO 조인 -- 게임방유저목록 추가해주고 
						
						//TODO 채팅창에 알려주고 
						
						//게임판 보여주고 
						this.showGamePane();
						
						
					}
					break;
				case Data.MAKE_ROOM:
					System.out.println(data.getUser() + "서버에서 보내는 데이터의 유저(최초생성자)");
					System.out.println(data.getUser().getId() + " 로부터" + loginUser + " 에게 Make_Room리스너 들어옴 ");

					// 만든 사람만 보여줄 퍼포먼스[게임 종류 상관없음]
					if (loginUser.getId().equals(data.getUser().getId())) {
						groupBtnRoom.setDisable(true);// 방만들었으니 만들기 버튼그룹 불활성화

					}
					// 타입구분, 게임룸 펼치기
					// 방만들어지면 해당 게임 선택한 사람들한테만 리스트 갱신해 줌
					if (loginUser.getSelectedGame() == data.getGameType()) {
						// 지금 내 게임 타입이 서버에서 보내주는 데이터에 기록된 게임 타입과 같은 경우 >>
						roomList = data.getRoomList();
						
						this.renewAllTable(roomList);

					}

					break;
				case Data.GAME_READY:
					break;
				case Data.GAME_START:
					break;
				case Data.DRAW_READY:
					if (loginUser.getId().equals(data.getUser().getId()) == false) {
						System.out.println("새 패스 시작");
						x = data.getGameInfo().getX_point();
						y = data.getGameInfo().getY_point();
						gc.moveTo(x, y);
						// gc.stroke();
						System.out.println("[시작 x,y :" + x + "," + y + "]");
					}
					break;
				case Data.DRAW_START:
					if (loginUser.getId().equals(data.getUser().getId()) == false) {
						// System.out.println(i + "수신");
						GameInfo received_ginfo = data.getGameInfo();
						System.out.println(x + "," + y + "새패스정보 드래그시 수신");// 찍는
																			// 위치
						xyArray = received_ginfo.getGeographicInfo();
						// gc.moveTo(x, y);

						// 배열로 받기
						for (double[] e : xyArray) {
							System.out.println(x + "," + y + "새패스정보 드래그시 수신(for)");// 찍는
																					// 위치
							double x = e[0];
							double y = e[1];
							gc.lineTo(x, y);
							gc.stroke();
							System.out.println(x + "," + y + "새패스정보 드래그시 수신(for122222)");
							// gc.closePath();//쓰면 채워지는 문제, 되지도 않고
							// gc.beginPath();//흐려지는 문제

						}
						xyArray.removeAll(xyArray);
						xyArray.clear();
						System.out.println("패스완성");
						// xyArray.removeAll(xyArray);//지워도 소용없네, 일단 배열의 초기화는
						// 선언시 하는게 나을지도

						// 드래그 끝나서 하나의 패스 완성
						i++;

					} // 게임데이터 송신자 이외 사람만 값 뽑아냄

					break;
				case Data.CLEAR_CANVAS:
					if (loginUser.getId().equals(data.getUser().getId()) == false) {
						this.clearActionOrder();
					}
					break;
				case Data.SEND_TURN:

					break;
				case Data.SEND_WINNING_RESULT:

					break;
				case Data.CHAT_MESSAGE:
					// TODO 게임룸 만들고 분기처리
					String received_msg = data.getMessage();
					txtArea_chatLog.appendText(received_msg + "\n");

					break;
				case Data.EXIT:
					System.out.println("로그아웃명령실행");
					connectedUserList = data.getUserList();
					renewalConUserList();

					// // 로그아웃한 사람만, 버튼에다가 넣자(이미 빠져서 와서 당사자 갱신 방법이 없음)
					// if (loginUser.getId().equals(data.getUser())) {
					// System.out.println("로그아웃한 사람만 실행");
					//
					// }

					break;
				default:
					break;

				}// switch

			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				exit = true; // 오류나면 socket , while 문 종료
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} // catch
		} // while
	}

	/**테이블 갱신 
	 * 셀렉트게임, 메이크룸에서 사용 
	 * @param roomList 
	 * 
	 */
	private void renewAllTable(HashMap<String, GameRoom> roomList) {
		column01.setCellValueFactory(new PropertyValueFactory<>("hostID"));
		column02.setCellValueFactory(new PropertyValueFactory<>("roomTitle"));
		tdata.clear();
		for (Entry<String, GameRoom> entry : roomList.entrySet()) {

			String roomID = entry.getValue().getRoomId();
			room = entry.getValue();

			String roomMakerId = entry.getValue().getUser().getId();
			String roomTitle = entry.getValue().getRoomId();
			String roomPw = entry.getValue().getRoomPw();

			System.out.println(roomMakerId);
			System.out.println(roomTitle);
			System.out.println(roomPw);

			makerID.add(roomMakerId);
			roomtitlearr.add(roomTitle);

			tdata.add(new modelClass(roomMakerId, roomTitle));
		}
		tableView.setItems(tdata);		
	}

	private void showloadingPane(long time) {

		loadingPane.setVisible(true);

		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		loadingPane.setVisible(false);

	}

	/**
	 * oos를 캡슐화한 데이터 보내기 메소드
	 */
	private void sendData(Data data) {
		try {
			oos.writeObject(data);
			oos.reset();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@FXML
	public void logOutAction() {
		data.setCommand(Data.EXIT);
		data.setUser(loginUser);
		sendData(data);
		System.out.println("로그아웃 명령 보냄");
		loginBox.setVisible(true);
		loginInfoPane.setVisible(false);

		// GUI활성화
		CommandPane.setDisable(true);
		txtArea_chatLog.setDisable(true);
		chatGroup.setDisable(true);
		roomListPane.setDisable(true);
		mainPane.setDisable(true);
	}

	@FXML
	private void renewalConUserList() {
		// TODO: connectedUserList 전체 접속자 리스트에 띄우기
		allUserList.setItems(null);// FIXME 지워주는 용도, 잘 작동할지는 모름
		ArrayList<String> connUserName = new ArrayList<String>();

		for (User user : connectedUserList) {
			String name = user.getId();
			connUserName.add(name);
		}

		ObservableList<Object> ob = FXCollections.observableArrayList(connUserName);
		allUserList.setItems(ob);
	}
	@FXML
	private void renewGameConUserList() {
		// TODO: connectedUserList 전체 접속자 리스트에 띄우기
		allUserList.setItems(null);// FIXME 지워주는 용도, 잘 작동할지는 모름
		ArrayList<String> connUserName = new ArrayList<String>();

		for (User user : connectedUserList) {
			String name = user.getId();
			connUserName.add(name);
		}

		ObservableList<Object> ob = FXCollections.observableArrayList(connUserName);
		allUserList.setItems(ob);
	}

	
	
	// //그림 그리는 메소드 연결해놓음
	// public void paintAction(MouseEvent event) {
	// gc.lineTo(event.getX(), event.getY());
	// gc.stroke();
	// }

	// 마우스 누를때 발생하는 동작을 정의한 액션리스너
	@FXML
	public void mousePress(MouseEvent event) {
		gc.beginPath();
		gc.moveTo(event.getX(), event.getY());
		gc.stroke();
		System.out.println(event.getX() + "," + event.getY() + "프레스송신");// 찍는 위치
		ginfo = new GameInfo(event.getX(), event.getY());

		data.setCommand(Data.DRAW_READY);
		data.setGameInfo(ginfo);
		this.sendData(data);
		geographicInfo = new ArrayList<double[]>();// 패스 발생 시점마다 배열 초기화되도록
													// flushing

	}

	int cnt = 1;
	private double x;
	private double y;

	ArrayList<double[]> geographicInfo;
	private ArrayList<double[]> xyArray;
	private String userMsg;
	private HashMap<String, GameRoom> roomList;
	private GameRoom room;
	private String myjoinRoomID;

	@FXML
	public void mouseDrag(MouseEvent event) {

		gc.lineTo(event.getX(), event.getY());
		gc.stroke();
		// ginfo = new GameInfo(event);
		double x = event.getX();
		double y = event.getY();
		double[] xy = { x, y };
		geographicInfo.add(xy);

	}

	// 마우스 놓을 때의 액션, 딱히 내용 없을 듯
	@FXML
	public void mouseRelease(MouseEvent event) {
		// for (double[][] e : geographicInfo) {
		// int i = e.
		// double x = xy[0][0];
		// double y = xy[0][1];
		//
		// }
		// for (double[] e : geographicInfo) {
		// double x = e[0];
		// double y = e[1];
		// ginfo.setX_point(x);
		// ginfo.setY_point(y);
		//
		// }
		System.out.println(geographicInfo + "드레그시 담긴 정보");// mouserPressed에서 한개의
															// 패스의 경로가 매번 새로
															// 담긴다.
		ginfo.setGeographicInfo(geographicInfo);

		// data.setUser(loginUser);
		data.setCommand(Data.DRAW_START);
		data.setGameInfo(ginfo);
		this.sendData(data);
		ginfo.setGeographicInfo(null);// VO에 안쌓이도록 flushing

	}

	// 초기 설정할 때 필요 한 듯 ...? 그런데 FXML에서 이미 초기설정 해주어서 바꿀 필요 있는 부분만 하면 될 듯
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gc = canvas.getGraphicsContext2D();
		startDraw(gc);

		// 슬라이더 선택값 변화 리슨
		slider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				double val = slider.getValue();
				gc.setLineWidth(val);
			}

		});

		// tableView.getItems();
		// System.out.println(tableView.getItems());

	}

	/**
	 * 네모칸 만들기
	 * 
	 * @param gc
	 */
	private void startDraw(GraphicsContext gc) {
		double canvasWidth = gc.getCanvas().getWidth();
		double canvasHeight = gc.getCanvas().getHeight();
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);

		gc.fill();
		gc.strokeRect(0, // x of the upper left corner
				0, // y of the upper left corner
				canvasWidth, // width of the rectangle
				canvasHeight); // height of the rectangle

	}

	/**
	 * 열고닫기 버튼 눌렀을 때 캔버스 사이즈조절하는 용도
	 * 
	 * @param gc
	 */
	private void RestartDraw(GraphicsContext gc) {
		double canvasWidth = canvasReign.getLayoutX();
		double canvasHeight = canvasReign.getMaxHeight();

		gc.setStroke(Color.BLACK);
		gc.setLineWidth(5);

		gc.fill();
		gc.strokeRect(0, // x of the upper left corner
				0, // y of the upper left corner
				canvasWidth, // width oButtonActionf the rectangle
				canvasHeight); // height of the rectangle

	}

	@FXML
	public void chatFieldEnterPressed(KeyEvent e) {
		// textfield에 뭔가 작성하고 나서 엔터 누르면 서버로 넘기게
		if (e.getCode() == KeyCode.ENTER) {
			// data 보내기
			userMsg = field_chat.getText();

			if (userMsg.equals("") == false) {
				data.setMessage("[ " + loginUser.getId() + " ] " + userMsg);
				data.setCommand(Data.CHAT_MESSAGE);
				this.sendData(data);
				field_chat.setText("");
			}
		}

	}

	/**
	 * 전송버튼 눌렀을 때
	 */
	@FXML
	public void sendAction(ActionEvent e) {
		userMsg = field_chat.getText();

		if (userMsg.equals("") == false) {
			data.setMessage("[ " + loginUser.getId() + " ] " + userMsg);
			data.setCommand(Data.CHAT_MESSAGE);
			this.sendData(data);
			field_chat.setText("");
		}

	}

	@FXML
	public void joinMouseAction(MouseEvent e) {
		modelClass mo = (modelClass) tableView.getSelectionModel().getSelectedItem();
		if (JOptionPane.showConfirmDialog(null, "해당 방에 입장하시겠습니까?", "방선택",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			myjoinRoomID = mo.getHostID();
			data.setJoinRoomID(myjoinRoomID);
			data.setCommand(Data.JOIN);
			this.sendData(data);

		}
	}
}
