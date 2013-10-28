package com.example.flocksourcingmx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Question_fragment extends Fragment implements View.OnClickListener {
	public static final String ARG_JSON_QUESTION = "Json question";
	public static final String ARG_QUESTION_POSITION = "Question position";
	public static final String ARG_CHAPTER_POSITION = "Chapter position";
	public static final String ARG_POSITION = null;
	private static String[] answerlist = null;
	private Toast toast;
	private String jquestionstring;
	private JSONObject jquestion = null;
	private JSONArray janswerlist = null;
	private String questionstring = "No questions on chapter";
	private Integer totalanswers;
	private TextView[] tvanswerlist = null;
	private Integer[] tvansweridlist = null;
	private String answerString;
	private String jumpString = null;
	private String answerjumpString = null;
	private ViewGroup answerlayout;
	private View rootView;
	private String questionkind = null;
	private Integer questionposition;
	private Integer chapterposition;
	private boolean other;
	private EditText otherET = null;
	private TextView otheranswer = null;
	private LinearLayout otherfield = null;
	private Integer othertotal = 0;

	public Question_fragment() {
		// Empty constructor required for fragment subclasses
	}

	// Information interface with main activity.

	// Passes Answer and jump information to activity.
	AnswerSelected Callback;

	// The container Activity must implement this interface so the fragment can
	// deliver messages
	public interface AnswerSelected {
		/** Called by Fragment when an answer is selected */
		public void AnswerRecieve(String answerString, String jumpString);
	}

	// Passes position information to activity. Will be called when fragment
	// resumes.
	PositionPasser Posback;

	public interface PositionPasser {
		/** Called by Fragment when an answer is selected */
		public void PositionRecieve(Integer chapterposition,
				Integer questionposition);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_question, container,
				false);

		// Getting json question and position in survey from parent activity.
		Bundle args = this.getArguments();
		if (args != null) {
			jquestionstring = args.getString(ARG_JSON_QUESTION);
			chapterposition = args.getInt(ARG_CHAPTER_POSITION);
			questionposition = args.getInt(ARG_QUESTION_POSITION);
		}
		try {
			jquestion = new JSONObject(jquestionstring);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			toast = Toast.makeText(getActivity(),
					"Question not recieved from main activity.",
					Toast.LENGTH_SHORT);
			toast.show();
		}

		// Setting up layout of fragment.

		answerlayout = (ViewGroup) rootView.findViewById(R.id.answerlayout); // Layout
																				// for
																				// the
																				// dynamic
																				// content
																				// of
																				// the
																				// activity
																				// (mainly
																				// answers).

		try {
			questionkind = jquestion.getString("Kind");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			questionkind = "no kind";
			toast = Toast.makeText(getActivity(),
					"No question kind in question.", Toast.LENGTH_SHORT);
			toast.show();
		}

		try {
			other = jquestion.getBoolean("Other");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			other = false;
			toast = Toast.makeText(getActivity(), "Other: " + other,
					Toast.LENGTH_SHORT);
			toast.show();
		}

		jumpString = getJump(jquestion);
		Callback.AnswerRecieve(answerString, jumpString);

		try {
			questionstring = jquestion.getString("Question");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Generating question kind specific layouts.
		if (questionkind.equals("MC")) {
			MultipleChoiceLayout();
		} else if (questionkind.equals("ON")) {
			OpenNumberLayout();
		} else if (questionkind.equals("OT")) {
			OpenTextLayout();
		} else if (questionkind.equals("CB")) {
			CheckBoxLayout();
		}

		TextView questionview = (TextView) rootView
				.findViewById(R.id.questionview);
		questionview.setText(questionstring);

		return rootView;

		// Changing background image, for debugging purpose.
		// int imageId = getResources().getIdentifier(
		// chapter.toLowerCase(Locale.getDefault()), "drawable",
		// getActivity().getPackageName());
		// ((ImageView) rootView.findViewById(R.id.image))
		// .setImageResource(imageId);
		//
		// // Setting activity titles as the chapter.
		// getActivity().setTitle(chapter);
		// return rootView;
	}

	@Override
	public void onResume() {
		// Communicates position of the question in the general suvey so the
		// position counter on the main activity can be updated accordingly.
		// Fixed a bug with the back stack where the main activity would get
		// lost in terms of its position on the survey.
		Posback.PositionRecieve(chapterposition, questionposition);
		super.onResume();
	}

	public void MultipleChoiceLayout() {
		JSONObject aux;
		try {
			janswerlist = jquestion.getJSONArray("Answers");
			totalanswers = janswerlist.length();
			answerlist = new String[totalanswers];
			tvanswerlist = new TextView[totalanswers];
			tvansweridlist = new Integer[totalanswers];
			for (int i = 0; i < totalanswers; ++i) {
				aux = janswerlist.getJSONObject(i);
				answerlist[i] = aux.getString("Answer");
				tvanswerlist[i] = new TextView(rootView.getContext());
				tvanswerlist[i].setText(answerlist[i]);
				tvanswerlist[i].setTextColor(getResources().getColor(
						R.color.text_color_light));
				answerlayout.addView(tvanswerlist[i]);
				tvansweridlist[i] = i; // Not sure if this is going to have
										// conflicts with other view ids...
				// tvansweridlist[i] = findId(); // This method was to generate
				// valid View ids that were not used by any other View, but it's
				// not
				// working.
				tvanswerlist[i].setId(tvansweridlist[i]);
				tvanswerlist[i].setOnClickListener(this);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			totalanswers = 0;
			toast = Toast.makeText(getActivity(),
					"Poblems with question parsing, please check surve file.",
					Toast.LENGTH_SHORT);
			toast.show();
		}
		if (other == Boolean.TRUE) {
			otherfield = new LinearLayout(rootView.getContext());
			otherfield.setOrientation(LinearLayout.VERTICAL);
			answerlayout.addView(otherfield);
			otherET = new EditText(rootView.getContext());
			otherET.setHint("Otro/Other");
			otherET.setImeOptions(EditorInfo.IME_ACTION_DONE);
			otherET.setSingleLine();
			// otherET.setInputType(InputType.TYPE_CLASS_NUMBER);
			otherET.setTextColor(getResources().getColor(
					R.color.text_color_light));
			answerlayout.addView(otherET);
			otherET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						++othertotal;
						otheranswer = new TextView(rootView.getContext());
						otheranswer.setText(otherET.getText());
						otherfield.addView(otheranswer);
						otheranswer.setId(othertotal + totalanswers - 1);
						otheranswer.setOnClickListener(Question_fragment.this);
						MultipleChoiceOnClick(otheranswer);
						otherET.setText("");
						return true;
					}
					return false;
				}
			});
		}

	}

	private void CheckBoxLayout() {
		// TODO Auto-generated method stub

	}

	private void OpenTextLayout() {
		// TODO Auto-generated method stub

	}

	private void OpenNumberLayout() {
		// TODO Auto-generated method stub

	}

	public String getJump(JSONObject Obj) {
		String auxjump;
		try {
			auxjump = Obj.getString("Jump");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			auxjump = null;
			e1.printStackTrace();
		}
		// toast = Toast.makeText(getActivity(), "Jump: " + auxjump,
		// Toast.LENGTH_SHORT);
		// toast.show();
		return auxjump;
	}

	@Override
	public void onClick(View view) {
		if (questionkind.equals("MC")) {
			MultipleChoiceOnClick(view);
		} else if (questionkind.equals("ON")) {
			OpenNumberOnClick(view);
		} else if (questionkind.equals("OT")) {
			OpenTextOnClick(view);
		} else if (questionkind.equals("CB")) {
			CheckBoxOnClick(view);
		}

	}

	public void MultipleChoiceOnClick(View view) {
		if (view instanceof TextView) {
			for (int i = 0; i < totalanswers; ++i) {

				TextView textView = (TextView) tvanswerlist[i];
				if (view.getId() == textView.getId()) {
					textView.setTextColor(getResources().getColor(
							R.color.answer_selected));
					try {
						answerjumpString = getJump(janswerlist.getJSONObject(i));
						if (answerjumpString != null) {
							jumpString = answerjumpString;
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					answerString = answerlist[i].toString(); // Sets the answer
																// to be sent to
																// parent
																// activity.
					Callback.AnswerRecieve(answerString, jumpString);
				} else {
					textView.setTextColor(getResources().getColor(
							R.color.text_color_light));
				}
			}
			if (othertotal >= 1) {
				for (int i = totalanswers; i < othertotal + totalanswers; ++i) {
					TextView textView = (TextView) rootView.findViewById(i);
					if (view.getId() == textView.getId()) {
						textView.setTextColor(getResources().getColor(
								R.color.answer_selected));
						answerString = (String) textView.toString(); // Sets the
																		// answer
																		// to be
																		// sent
																		// to
																		// parent
																		// activity.
						Callback.AnswerRecieve(answerString, jumpString);
					} else {
						textView.setTextColor(getResources().getColor(
								R.color.text_color_light));
					}
				}
			}
		}
	}

	private void CheckBoxOnClick(View view) {
		// TODO Auto-generated method stub

	}

	private void OpenTextOnClick(View view) {
		// TODO Auto-generated method stub

	}

	private void OpenNumberOnClick(View view) {
		// TODO Auto-generated method stub

	}

	// public int findId(){
	// Integer id = 1;
	// View v = getActivity().findViewById(id);
	// while (v != null){
	// v = getActivity().findViewById(++id);
	// }
	// return id++;
	// }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			Callback = (AnswerSelected) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement Question_fragment.AnswerSelected");
		}
		try {
			Posback = (PositionPasser) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement Question_fragment.PositionPasser");
		}
	}

}