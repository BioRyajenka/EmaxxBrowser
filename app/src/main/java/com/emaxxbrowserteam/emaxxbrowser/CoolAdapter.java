package com.emaxxbrowserteam.emaxxbrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emaxxbrowserteam.emaxxbrowser.model.Algorithm;
import com.emaxxbrowserteam.emaxxbrowser.model.SuperTopic;
import com.emaxxbrowserteam.emaxxbrowser.model.Topic;

import java.util.ArrayList;
import java.util.List;

public class CoolAdapter extends BaseExpandableListAdapter {

	public List<SuperTopic> getSuperTopics() {
		return originalList;
	}

    private List<SuperTopic> originalList;
    private List<SuperTopic> filteredList;
	private Context mContext;

	public CoolAdapter(Context context, List<SuperTopic> groups) {
		mContext = context;
        originalList = groups;
        filteredList = new ArrayList<>();
        filterData("");
	}

	@Override
	public int getGroupCount() {
		return filteredList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return filteredList.get(groupPosition).getTopicsCount();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return filteredList.get(groupPosition).getTitle();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return filteredList.get(groupPosition).getTopic(childPosition).getTitle();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
			ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.group_view, null);
		}

		if (isExpanded) {
            convertView.setBackgroundResource(R.drawable.list_item_bg_pressed);
		} else {
            convertView.setBackgroundResource(R.drawable.list_item_bg_normal);
		}

		ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

		txtTitle.setText(filteredList.get(groupPosition).getTitle());

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.child_view, null);
		}

        Topic topic = filteredList.get(groupPosition).getTopic(childPosition);

        convertView.setTag(topic);

		TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
		textChild.setText(topic.getTitle());

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void filterData(String query) {
        query = query.toLowerCase();
        filteredList.clear();
        if (query.equals("")) {
            filteredList.addAll(originalList);
        } else {
            for (SuperTopic st : originalList) {
                SuperTopic nst = new SuperTopic(st.getTitle());
                for (Topic t : st.topics) {
                    boolean ok = false;
                    for (Algorithm a : t.algorithms) {
                        ok |= (a.getTitle().contains(query));
                    }
                    if (ok) {
                        nst.add(t);
                    }
                }
                if (!nst.isEmpty()) {
                    filteredList.add(nst);
                }
            }
        }
        notifyDataSetChanged();
    }
}